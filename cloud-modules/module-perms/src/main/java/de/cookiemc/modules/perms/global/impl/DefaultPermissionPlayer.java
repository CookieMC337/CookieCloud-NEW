package de.cookiemc.modules.perms.global.impl;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.permission.Permission;
import de.cookiemc.driver.permission.PermissionGroup;
import de.cookiemc.driver.permission.PermissionManager;
import de.cookiemc.driver.permission.PermissionPlayer;
import de.cookiemc.driver.player.CloudOfflinePlayer;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.player.ICloudPlayerManager;
import de.cookiemc.driver.services.task.ICloudServiceTaskManager;
import de.cookiemc.driver.services.task.IServiceTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class DefaultPermissionPlayer implements PermissionPlayer {

    private String name;
    private UUID uniqueId;
    private Map<String, Long> permissions; //<Name, Instance>

    private Map<String, Long> groups; //<Name, TimeOut>

    @Setter
    private Collection<String> deniedPermissions;

    private Map<String, Collection<String>> taskPermissions;

    public DefaultPermissionPlayer(String name, UUID uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId;

        this.permissions = new HashMap<>();
        this.groups = new HashMap<>();

        this.deniedPermissions = new ArrayList<>();
        this.taskPermissions = new ConcurrentHashMap<>();
    }

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {

        switch (state) {
            case READ:
                name = buf.readString();
                uniqueId = buf.readUniqueId();

                permissions = new HashMap<>();
                for (DefaultPermission defaultPermission : buf.readObjectCollection(DefaultPermission.class)) {
                    addPermission(defaultPermission);
                }
                this.groups = buf.readMap(PacketBuffer::readString, PacketBuffer::readLong);
                this.deniedPermissions = buf.readStringCollection();
                this.taskPermissions = buf.readMap(PacketBuffer::readString, PacketBuffer::readStringCollection);
                break;

            case WRITE:
                buf.writeString(name);
                buf.writeUniqueId(uniqueId);
                buf.writeObjectCollection(this.getPermissions());
                buf.writeMap(groups, PacketBuffer::writeString, PacketBuffer::writeLong);
                buf.writeStringCollection(getDeniedPermissions());
                buf.writeMap(taskPermissions, PacketBuffer::writeString, PacketBuffer::writeStringCollection);
                break;
        }
    }

    @Override
    public Map<IServiceTask, Collection<String>> getTaskPermissions() {
        Map<IServiceTask, Collection<String>> taskPermissions = new ConcurrentHashMap<>();
        for (Map.Entry<String, Collection<String>> e : this.taskPermissions.entrySet()) {
            taskPermissions.put(CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).getTaskOrNull(e.getKey()), e.getValue());
        }
        return taskPermissions;
    }

    @Override
    public void addDeniedPermission(String permission) {
        this.deniedPermissions.add(permission);
    }

    @Override
    public void removeDeniedPermission(String permission) {
        this.deniedPermissions.remove(permission);
    }

    @Override
    public void addTaskPermission(IServiceTask task, String permission) {
        Collection<String> taskPermissions = this.getTaskPermissions(task.getName());
        if (!taskPermissions.contains(permission)) {
            taskPermissions.add(permission);
        }
        this.taskPermissions.put(task.getName(), taskPermissions);
    }

    @Override
    public void removeTaskPermission(IServiceTask task, String permission) {

        Collection<String> taskPermissions = this.getTaskPermissions(task.getName());
        taskPermissions.remove(permission);

        this.taskPermissions.put(task.getName(), taskPermissions);
    }

    @Override
    public void setTaskPermissions(Map<IServiceTask, Collection<String>> taskPermissions) {
        for (Map.Entry<IServiceTask, Collection<String>> e : taskPermissions.entrySet()) {
            this.taskPermissions.put(e.getKey().getName(), e.getValue());
        }
    }

    @Override
    public void checkForExpiredValues() {
        boolean modifiedGroups = this.testGroups();
        boolean modifiedPerms = this.testPerms();

        if (modifiedGroups || modifiedPerms) {
            this.update();
        }
    }

    public boolean testGroups() {
        long currentTime = System.currentTimeMillis();
        int sizeBefore = groups.size();
        for (String groupName : groups.keySet()) {
            long timeOut = groups.get(groupName);
            if (timeOut != -1 && currentTime > timeOut || CloudDriver.getInstance().getProviderRegistry().getUnchecked(PermissionManager.class).getPermissionGroupByNameOrNull(groupName) == null) {
                groups.remove(groupName);
            }
        }
        if (sizeBefore != groups.size())
            CloudDriver.getInstance().getLogger().trace("Removed expired groups from {}", this.getName());
        return sizeBefore != groups.size();
    }


    public boolean testPerms() {
        int sizeBefore = permissions.size();
        for (String permission : permissions.keySet()) {
            Permission dp = Permission.of(permission, permissions.get(permission));
            if (dp.hasExpired()) {
                permissions.remove(permission);
            }
        }
        if (sizeBefore != permissions.size())
            CloudDriver.getInstance().getLogger().trace("Removed expired perms from {}", this.getName());
        return sizeBefore != permissions.size();
    }


    @Override
    public void addPermission(Permission permission) {
        this.permissions.put(permission.getPermission(), permission.getExpirationDate());
    }

    @Override
    public void removePermission(Permission permission) {
        this.permissions.remove(permission.getPermission());
    }

    @Override
    public Collection<Permission> getPermissions() {
        this.checkForExpiredValues();
        return this.permissions.keySet().stream().map(p -> Permission.of(p, this.permissions.get(p))).collect(Collectors.toList());
    }

    @Override
    public Task<Permission> getPermission(String permission) {
        this.checkForExpiredValues();
        return Task.callAsync(() -> this.permissions.containsKey(permission) ? Permission.of(permission, this.permissions.getOrDefault(permission, -1L)) : null);
    }

    @Override
    public Permission getPermissionOrNull(String permission) {
        this.checkForExpiredValues();
        return this.permissions.containsKey(permission) ? Permission.of(permission, this.permissions.getOrDefault(permission, -1L)) : null;
    }

    @Override
    public boolean hasPermission(String permission) {
        this.checkForExpiredValues();
        Permission perm = this.getPermissionOrNull(permission);
        if (perm == null) {
            for (PermissionGroup group : getPermissionGroups()) {
                if (group.hasPermission(permission)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean hasPermission(Permission permission) {

        this.checkForExpiredValues();
        if (this.getPermission(permission.getPermission()).isPresent()) {
            return true;
        } else {
            for (PermissionGroup group : getPermissionGroups()) {
                if (group.hasPermission(permission) && !group.getDeniedPermissions().contains(permission.getPermission())) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void update() {
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(PermissionManager.class).updatePermissionPlayer(this);
    }

    @Nullable
    @Override
    public ICloudPlayer toOnlinePlayer() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getCloudPlayerByUniqueIdOrNull(uniqueId);
    }

    @NotNull
    @Override
    public CloudOfflinePlayer toOfflinePlayer() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getOfflinePlayerByUniqueIdBlockingOrNull(uniqueId);
    }

    @NotNull
    @Override
    public Collection<PermissionGroup> getPermissionGroups() {
        this.checkForExpiredValues();
        return this.groups.keySet().stream().map(s -> CloudDriver.getInstance().getProviderRegistry().getUnchecked(PermissionManager.class).getPermissionGroupByNameOrNull(s)).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public PermissionGroup getHighestGroup() {
        this.checkForExpiredValues();
        return getPermissionGroups().stream().min(Comparator.comparingInt(PermissionGroup::getSortId)).orElse(null);
    }

    @Override
    public boolean isInPermissionGroup(String name) {
        this.checkForExpiredValues();
        return groups.get(name) != null;
    }

    @Override
    public void addPermissionGroup(@NotNull PermissionGroup group) {
        this.groups.put(group.getName(), -1L);
    }

    @Override
    public void addPermissionGroup(@NotNull PermissionGroup group, TimeUnit unit, long value) {
        this.groups.put(group.getName(), (System.currentTimeMillis() + unit.toMillis(value)));
    }

    @Override
    public void removePermissionGroup(String groupName) {
        this.groups.remove(groupName);
    }
}
