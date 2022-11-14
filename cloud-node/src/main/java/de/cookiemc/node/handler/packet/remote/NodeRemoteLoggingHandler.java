package de.cookiemc.node.handler.packet.remote;

import de.cookiemc.driver.networking.NetworkComponent;
import de.cookiemc.driver.networking.protocol.packets.defaults.DriverLoggingPacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.node.NodeDriver;

public class NodeRemoteLoggingHandler implements PacketHandler<DriverLoggingPacket> {

    @Override
    public void handle(PacketChannel wrapper, DriverLoggingPacket packet) {
        NetworkComponent component = packet.getComponent();
        String message = packet.getMessage();

        if (component.getName().equalsIgnoreCase(NodeDriver.getInstance().getNode().getName())) {
            NodeDriver.getInstance().getLogger().info(message);
        }
    }
}