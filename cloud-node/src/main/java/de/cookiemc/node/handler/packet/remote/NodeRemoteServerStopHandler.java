package de.cookiemc.node.handler.packet.remote;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.node.packet.NodeRequestServerStopPacket;
import de.cookiemc.driver.networking.protocol.packets.NetworkResponseState;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.ICloudServiceManager;
import de.cookiemc.node.NodeDriver;

public class NodeRemoteServerStopHandler implements PacketHandler<NodeRequestServerStopPacket> {

    @Override
    public void handle(PacketChannel wrapper, NodeRequestServerStopPacket packet) {
        String server = packet.getServerName();
        Task<ICloudServer> service = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getServiceAsync(server);
        service.ifPresent(s -> NodeDriver.getInstance().getNode().stopServer(s));
        if (packet.isDemandsResponse()) {
            wrapper.prepareResponse().state(service.isPresent() ? NetworkResponseState.OK : NetworkResponseState.FAILED).execute(packet);
        }
    }
}
