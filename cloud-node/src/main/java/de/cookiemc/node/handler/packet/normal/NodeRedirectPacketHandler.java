package de.cookiemc.node.handler.packet.normal;

import de.cookiemc.driver.networking.cluster.ClusterClientExecutor;
import de.cookiemc.driver.networking.protocol.packets.defaults.RedirectPacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.node.NodeDriver;
import de.cookiemc.node.config.MainConfiguration;
import de.cookiemc.node.node.NodeBasedClusterExecutor;

import java.util.Optional;

public class NodeRedirectPacketHandler implements PacketHandler<RedirectPacket> {

    @Override
    public void handle(PacketChannel wrapper, RedirectPacket packet) {

        if (packet.getClient().equalsIgnoreCase(MainConfiguration.getInstance().getNodeConfig().getNodeName())) {
            NodeDriver.getInstance().getNetworkExecutor().handlePacket(wrapper, packet.getPacket()); //handle if should be redirected to this node
            return;
        }
        NodeBasedClusterExecutor executor = NodeDriver.getInstance().getNetworkExecutor();
        Optional<ClusterClientExecutor> client = executor.getClient(packet.getClient());
        client.ifPresent(s -> s.sendPacket(packet.getPacket()));
    }
}
