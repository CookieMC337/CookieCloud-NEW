package de.cookiemc.modules.perms;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.ICommandManager;
import de.cookiemc.driver.database.IDatabaseManager;
import de.cookiemc.driver.database.SectionedDatabase;
import de.cookiemc.driver.module.ModuleController;
import de.cookiemc.driver.module.controller.base.ModuleState;
import de.cookiemc.driver.module.controller.task.ModuleTask;
import de.cookiemc.driver.networking.PacketProvider;
import de.cookiemc.driver.permission.Permission;
import de.cookiemc.driver.permission.PermissionGroup;
import de.cookiemc.driver.permission.PermissionManager;
import de.cookiemc.modules.perms.cloud.ModulePermissionManager;
import de.cookiemc.modules.perms.cloud.command.PermsCommand;
import de.cookiemc.modules.perms.cloud.handler.GroupPacketHandler;
import de.cookiemc.modules.perms.cloud.handler.PlayerPacketHandler;
import de.cookiemc.modules.perms.cloud.handler.PlayerUpdatePacketHandler;
import de.cookiemc.modules.perms.global.impl.DefaultPermissionGroup;
import de.cookiemc.modules.perms.global.impl.DefaultPermissionPlayer;
import de.cookiemc.modules.perms.global.impl.ParameterPermissionGroup;
import de.cookiemc.modules.perms.global.impl.ParameterPermissionPlayer;
import de.cookiemc.modules.perms.global.packets.PermsCacheUpdatePacket;
import de.cookiemc.modules.perms.global.packets.PermsGroupPacket;
import de.cookiemc.modules.perms.global.packets.PermsPlayerRequestPacket;
import de.cookiemc.modules.perms.global.packets.PermsPlayerUpdatePacket;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class PermsModule {

    @Getter
    private final ModuleController controller;

    public PermsModule(ModuleController controller) {
        this.controller = controller;
    }

    @ModuleTask(id = 1, state = ModuleState.LOADED)
    public void load() {
        PacketProvider.autoRegister(PermsCacheUpdatePacket.class);
        PacketProvider.autoRegister(PermsGroupPacket.class);
        PacketProvider.autoRegister(PermsPlayerRequestPacket.class);
        PacketProvider.autoRegister(PermsPlayerUpdatePacket.class);

        CloudDriver.getInstance()
                .getProviderRegistry()
                .setProvider(PermissionManager.class, new ModulePermissionManager());
    }

    @ModuleTask(id = 2, state = ModuleState.ENABLED)
    public void enable() {

        SectionedDatabase database = CloudDriver.getInstance().getProviderRegistry().getUnchecked(IDatabaseManager.class).getDatabase();
        database.registerSection("module-perms-groups", DefaultPermissionGroup.class);
        database.registerSection("module-perms-players", DefaultPermissionPlayer.class);

        PermissionManager pm = CloudDriver.getInstance().getProviderRegistry().getUnchecked(PermissionManager.class);
        ((ModulePermissionManager) pm).loadGroups(); //loading groups

        if (pm.getAllCachedPermissionGroups().isEmpty()) {

            PermissionGroup permissionGroup = pm.createPermissionGroup("Player");
            permissionGroup.setDefaultGroup(true);
            permissionGroup.setChatColor("&7");
            permissionGroup.setPrefix("&7");
            permissionGroup.setSuffix("&7");
            permissionGroup.setSortId(9999);
            permissionGroup.setNamePrefix("&7");
            permissionGroup.addPermission(Permission.of("cloud.test.permanent.permission"));
            permissionGroup.addPermission(Permission.of("cloud.test.temporary.permission", TimeUnit.DAYS, 30));

            permissionGroup.update(); //make sure it's saved

            PermissionGroup nextGroup = pm.createPermissionGroup("Admin");
            nextGroup.setDefaultGroup(false);
            nextGroup.setChatColor("&4");
            nextGroup.setPrefix("&4");
            nextGroup.setSuffix("&7");
            nextGroup.setSortId(0);
            nextGroup.setNamePrefix("&4");
            nextGroup.addPermission(Permission.of("*"));
            nextGroup.addInheritedGroup("Player");

            nextGroup.update(); //make sure it's saved
        }

        //registering network handler
        CloudDriver.getInstance().getNetworkExecutor().registerPacketHandler(new GroupPacketHandler());
        CloudDriver.getInstance().getNetworkExecutor().registerPacketHandler(new PlayerPacketHandler());
        CloudDriver.getInstance().getNetworkExecutor().registerPacketHandler(new PlayerUpdatePacketHandler());

        //registering commands and parsers
        CloudDriver.
                getInstance()
                .getProviderRegistry()
                .getUnchecked(ICommandManager.class)
                .registerCommands(new PermsCommand());

        CloudDriver.
                getInstance()
                .getProviderRegistry()
                .getUnchecked(ICommandManager.class)
                .getParamTypeRegistry()
                .register(
                        new ParameterPermissionPlayer(),
                        new ParameterPermissionGroup()
                );
    }
}
