package de.cookiemc.node.handler.packet.remote;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.node.packet.NodeRequestServerStartPacket;
import de.cookiemc.driver.networking.protocol.packets.NetworkResponseState;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.ICloudServiceManager;

public class NodeRemoteServerStartHandler implements PacketHandler<NodeRequestServerStartPacket> {

    @Override
    public void handle(PacketChannel wrapper, NodeRequestServerStartPacket packet) {
        ICloudServer server = packet.getServer();
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).startService(server)
                .onTaskSucess(cloudServer -> {
                    if (packet.isDemandsResponse()) {
                        wrapper.prepareResponse().state(NetworkResponseState.OK).execute(packet);
                    }
                })
                .onTaskFailed(throwable -> {
                    if (packet.isDemandsResponse()) {
                        wrapper.prepareResponse().error(throwable).state(NetworkResponseState.FAILED).execute(packet);
                    }
                });
    }
}
