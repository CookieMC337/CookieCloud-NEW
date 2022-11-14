package de.cookiemc.bridge.universal;

import de.cookiemc.bridge.CloudBridge;
import de.cookiemc.driver.networking.protocol.packets.defaults.DriverUpdatePacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.remote.adapter.IBridgeProxyExtension;

public class UniversalBridgeUpdateHandler implements PacketHandler<DriverUpdatePacket> {

    @Override
    public void handle(PacketChannel wrapper, DriverUpdatePacket packet) {
        IBridgeProxyExtension proxy = CloudBridge.getRemoteExtension().asProxyExtension();

        if (proxy == null) {
            return; //no proxy environment
        }

        proxy.clearServices();
        for (ICloudServer allCachedService : packet.getAllCachedServices()) {
            proxy.registerService(allCachedService);
        }

    }
}
