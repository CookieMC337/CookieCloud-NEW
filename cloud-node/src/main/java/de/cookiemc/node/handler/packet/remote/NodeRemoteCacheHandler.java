package de.cookiemc.node.handler.packet.remote;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.networking.protocol.packets.defaults.DriverUpdatePacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.node.INodeManager;
import de.cookiemc.driver.player.ICloudPlayerManager;
import de.cookiemc.driver.services.ICloudServiceManager;
import de.cookiemc.driver.services.task.ICloudServiceTaskManager;
import de.cookiemc.node.NodeDriver;

public class NodeRemoteCacheHandler implements PacketHandler<DriverUpdatePacket> {

    @Override
    public void handle(PacketChannel wrapper, DriverUpdatePacket packet) {
        CloudDriver.getInstance().getLogger().info(
                "Received Cache: [{} Servers] [{} Tasks] [{} Groups] [{} Players] [{} Nodes]",
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getAllCachedServices().size(),
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).getAllCachedTasks().size(),
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).getAllCachedTaskGroups().size(),
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getAllCachedCloudPlayers().size(),
                NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).getAllCachedNodes().size()
        );
    }
}
