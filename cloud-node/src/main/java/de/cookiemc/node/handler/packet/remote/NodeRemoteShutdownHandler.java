package de.cookiemc.node.handler.packet.remote;

import de.cookiemc.driver.node.packet.NodeRequestShutdownPacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.node.NodeDriver;

public class NodeRemoteShutdownHandler implements PacketHandler<NodeRequestShutdownPacket> {

    @Override
    public void handle(PacketChannel wrapper, NodeRequestShutdownPacket packet) {
        if (packet.getName().equalsIgnoreCase(NodeDriver.getInstance().getNode().getName())) {
            NodeDriver.getInstance().getNode().shutdown();
        }
    }
}
