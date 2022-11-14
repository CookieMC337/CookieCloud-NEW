package de.cookiemc.driver.networking.protocol.packets.defaults;

import de.cookiemc.common.logging.Logger;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.driver.DriverCacheUpdateEvent;
import de.cookiemc.driver.networking.IPacketExecutor;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.driver.node.INode;
import de.cookiemc.driver.node.INodeManager;
import de.cookiemc.driver.node.UniversalNode;
import de.cookiemc.driver.player.ICloudPlayerManager;
import de.cookiemc.driver.player.impl.UniversalCloudPlayer;
import de.cookiemc.driver.CloudDriver;

import de.cookiemc.driver.services.ICloudServiceManager;
import de.cookiemc.driver.services.task.ICloudServiceTaskManager;
import de.cookiemc.driver.services.task.IServiceTask;
import de.cookiemc.driver.services.task.UniversalServiceTask;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.task.bundle.ITaskGroup;
import de.cookiemc.driver.services.task.bundle.DefaultTaskGroup;
import de.cookiemc.driver.services.impl.UniversalCloudServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class DriverUpdatePacket extends AbstractPacket {

    private Collection<IServiceTask> serviceTasks;
    private Collection<ITaskGroup> parentGroups;
    private Collection<ICloudServer> allCachedServices;
    private Collection<ICloudPlayer> cloudPlayers;
    private Collection<INode> connectedNodes;


    public static void publishUpdate(IPacketExecutor sender) {
        sender.sendPacket(new DriverUpdatePacket());

        Logger.constantInstance().debug("Published Update using {}", sender);
    }

    public DriverUpdatePacket() {
        this(
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).getAllCachedTasks(),
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).getAllCachedTaskGroups(),
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getAllCachedServices(),
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getAllCachedCloudPlayers(),
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).getAllCachedNodes()
        );
    }

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {

        //do not modify order of reading / writing

        switch (state) {

            case READ:

                parentGroups = buf.readWrapperObjectCollection(DefaultTaskGroup.class);
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).setAllCachedTaskGroups(parentGroups);

                serviceTasks = buf.readWrapperObjectCollection(UniversalServiceTask.class);
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).setAllCachedTasks(serviceTasks);

                allCachedServices = buf.readWrapperObjectCollection(UniversalCloudServer.class);
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).setAllCachedServices((List<ICloudServer>) allCachedServices);

                cloudPlayers = buf.readWrapperObjectCollection(UniversalCloudPlayer.class);
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).setAllCachedCloudPlayers((List<ICloudPlayer>) cloudPlayers);

                connectedNodes = buf.readWrapperObjectCollection(UniversalNode.class);
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).setAllCachedNodes((List<INode>) connectedNodes);

                CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventOnlyLocally(new DriverCacheUpdateEvent());
                break;

            case WRITE:
                buf.writeObjectCollection(parentGroups);
                buf.writeObjectCollection(serviceTasks);
                buf.writeObjectCollection(allCachedServices);
                buf.writeObjectCollection(cloudPlayers);
                buf.writeObjectCollection(connectedNodes);
                break;
        }
    }
}
