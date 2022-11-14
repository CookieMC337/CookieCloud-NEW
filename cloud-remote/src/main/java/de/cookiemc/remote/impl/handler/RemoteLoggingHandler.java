package de.cookiemc.remote.impl.handler;

import de.cookiemc.driver.networking.NetworkComponent;
import de.cookiemc.driver.networking.protocol.packets.defaults.DriverLoggingPacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.remote.Remote;

public class RemoteLoggingHandler implements PacketHandler<DriverLoggingPacket> {

    @Override
    public void handle(PacketChannel wrapper, DriverLoggingPacket packet) {

        NetworkComponent component = packet.getComponent();
        String message = packet.getMessage();

        if (component.matches(Remote.getInstance().getNetworkExecutor())) {
            Remote.getInstance().getLogger().info(message);
        }
    }
}
