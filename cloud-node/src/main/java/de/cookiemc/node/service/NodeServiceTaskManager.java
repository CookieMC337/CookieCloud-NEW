package de.cookiemc.node.service;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.database.IDatabaseManager;
import de.cookiemc.driver.event.EventListener;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.task.TaskUpdateEvent;

import de.cookiemc.driver.networking.protocol.packets.defaults.DriverUpdatePacket;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.node.INodeManager;
import de.cookiemc.driver.services.task.DefaultServiceTaskManager;
import de.cookiemc.driver.services.task.packet.ServiceTaskExecutePacket;
import de.cookiemc.driver.services.task.IServiceTask;
import de.cookiemc.driver.services.task.bundle.ITaskGroup;
import de.cookiemc.driver.services.template.ITemplate;
import de.cookiemc.driver.services.template.ITemplateStorage;
import de.cookiemc.node.NodeDriver;
import de.cookiemc.driver.database.SectionedDatabase;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;


public class NodeServiceTaskManager extends DefaultServiceTaskManager implements PacketHandler<ServiceTaskExecutePacket> {

    private final SectionedDatabase database;

    public NodeServiceTaskManager() {
        this.database = NodeDriver.getInstance().getProviderRegistry().getUnchecked(IDatabaseManager.class).getDatabase();

        // loading all database groups and configurations
        this.getAllCachedTaskGroups().addAll(this.database.getSection(ITaskGroup.class).getAll());
        this.getAllCachedTasks().addAll(this.database.getSection(IServiceTask.class).getAll());

        if (CloudDriver.getInstance().getNetworkExecutor() != null) {

            //registering packet handler
            CloudDriver.getInstance().getNetworkExecutor().registerPacketHandler(this);

            //registering events
            CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).registerListener(this);

        }

        if (CloudDriver.getInstance().getNetworkExecutor() == null) {
            //setup state
            return;
        }
        if (this.getAllCachedTasks().isEmpty()) {
            CloudDriver.getInstance().getLogger().warn("There are no ServiceTasks loaded!");
            CloudDriver.getInstance().getLogger().warn("Maybe you want to create some?");
        } else {
            CloudDriver.getInstance().getLogger().info("§7Cached following TaskGroups: §b" + this.getAllCachedTaskGroups().stream().map(ITaskGroup::getName).collect(Collectors.joining("§8, §b")));
            CloudDriver.getInstance().getLogger().info("§7Cached following ServiceTasks: §b" + this.getAllCachedTasks().stream().map(IServiceTask::getName).collect(Collectors.joining("§8, §b")));
        }

    }


    @EventListener
    public void handle(TaskUpdateEvent event) {
        IServiceTask packetTask = event.getTask();
        IServiceTask task = getTaskOrNull(packetTask.getName());

        if (task == null) {
            return;
        }

        CloudDriver.getInstance().getLogger().trace("Updated Task {}", task.getName());
        task.copy(packetTask);
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventOnlyPacketBased(new TaskUpdateEvent(task));

        NodeDriver.getInstance().getServiceQueue().dequeue();

        if (NodeDriver.getInstance().getProviderRegistry().get(INodeManager.class).isNull()) {
            return;
        }
        if (NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class) != null && NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).isHeadNode()) {
            DriverUpdatePacket.publishUpdate(CloudDriver.getInstance().getNetworkExecutor());
        }
    }

    @Override
    public void registerTask(@NotNull IServiceTask task) {
        this.database.getSection(IServiceTask.class).insert(task.getName(), task);
        if (NodeDriver.getInstance().getNetworkExecutor() != null) {
            NodeDriver.getInstance().getNetworkExecutor().sendPacketToAll(new ServiceTaskExecutePacket(task, ServiceTaskExecutePacket.ExecutionPayLoad.CREATE));
        }
        super.registerTask(task);

        if (NodeDriver.getInstance().getProviderRegistry().get(INodeManager.class).isNull()) {
            return;
        }
        if (NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class) != null && NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).isHeadNode()) {
            DriverUpdatePacket.publishUpdate(CloudDriver.getInstance().getNetworkExecutor());
        }
    }

    @Override
    public void registerTaskGroup(@NotNull ITaskGroup task) {
        this.database.getSection(ITaskGroup.class).insert(task.getName(), task);
        super.registerTaskGroup(task);

        if (NodeDriver.getInstance().getProviderRegistry().get(INodeManager.class).isNull()) {
            return;
        }
        if (NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class) != null && NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).isHeadNode()) {
            DriverUpdatePacket.publishUpdate(CloudDriver.getInstance().getNetworkExecutor());
        }
    }

    @Override
    public void unregisterTaskGroup(@NotNull ITaskGroup task) {
        this.database.getSection(ITaskGroup.class).delete(task.getName());
        super.unregisterTaskGroup(task);

        if (NodeDriver.getInstance().getProviderRegistry().get(INodeManager.class).isNull()) {
            return;
        }
        if (NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class) != null && NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).isHeadNode()) {
            DriverUpdatePacket.publishUpdate(CloudDriver.getInstance().getNetworkExecutor());
        }
    }

    @Override
    public void unregisterTask(@NotNull IServiceTask task) {
        this.database.getSection(IServiceTask.class).delete(task.getName());
        if (NodeDriver.getInstance().getNetworkExecutor() != null) {
            NodeDriver.getInstance().getNetworkExecutor().sendPacketToAll(new ServiceTaskExecutePacket(task, ServiceTaskExecutePacket.ExecutionPayLoad.REMOVE));
        }
        super.unregisterTask(task);

        if (NodeDriver.getInstance().getProviderRegistry().get(INodeManager.class).isNull()) {
            return;
        }
        if (NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class) != null && NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).isHeadNode()) {
            DriverUpdatePacket.publishUpdate(CloudDriver.getInstance().getNetworkExecutor());
        }
    }

    @Override
    public void updateTask(@NotNull IServiceTask task) {
        this.database.getSection(IServiceTask.class).update(task.getName(), task);
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new TaskUpdateEvent(task));


        if (NodeDriver.getInstance().getProviderRegistry().get(INodeManager.class).isNull()) {
            return;
        }
        if (NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class) != null && NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).isHeadNode()) {
            DriverUpdatePacket.publishUpdate(CloudDriver.getInstance().getNetworkExecutor());
        }
    }

    @Override
    public void handle(PacketChannel wrapper, ServiceTaskExecutePacket packet) {

        if (packet.getPayLoad().equals(ServiceTaskExecutePacket.ExecutionPayLoad.CREATE)) {
            this.getAllCachedTasks().add(packet.getServiceTask());

            //creating templates
            for (ITemplate template : packet.getServiceTask().getTaskGroup().getTemplates()) {
                ITemplateStorage storage = template.getStorage();
                if (storage != null) {
                    storage.createTemplate(template);
                }
            }

            NodeDriver.getInstance().getServiceQueue().dequeue();
        } else {
            this.getAllCachedTasks().remove(packet.getServiceTask());
        }
    }
}
