package de.cookiemc.node.handler.packet.normal;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.services.packet.ServiceRequestShutdownPacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.ICloudServiceManager;

public class NodeServiceShutdownHandler implements PacketHandler<ServiceRequestShutdownPacket> {

    @Override
    public void handle(PacketChannel wrapper, ServiceRequestShutdownPacket packet) {
        String serverName = packet.getService();
        ICloudServiceManager serviceManager = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class);
        ICloudServer service = serviceManager.getService(serverName);

        if (service != null) {
            service.shutdown();
        }
    }
}
