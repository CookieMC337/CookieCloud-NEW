package de.cookiemc.bridge.universal;

import de.cookiemc.bridge.CloudBridge;
import de.cookiemc.driver.services.packet.ServiceCommandPacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.remote.adapter.IBridgeExtension;

public class UniversalBridgeCommandHandler implements PacketHandler<ServiceCommandPacket> {

    @Override
    public void handle(PacketChannel wrapper, ServiceCommandPacket packet) {
        IBridgeExtension extension = CloudBridge.getRemoteExtension();
        if (extension == null) {
            return;
        }
        extension.executeCommand(packet.getCommand());
    }
}
