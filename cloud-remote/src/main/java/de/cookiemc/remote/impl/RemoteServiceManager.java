package de.cookiemc.remote.impl;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.event.EventListener;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.server.ServiceRegisterEvent;
import de.cookiemc.driver.event.defaults.server.ServiceUnregisterEvent;
import de.cookiemc.driver.event.defaults.server.ServiceUpdateEvent;
import de.cookiemc.driver.networking.protocol.packets.defaults.RedirectPacket;
import de.cookiemc.driver.node.packet.NodeRequestServerStartPacket;
import de.cookiemc.driver.services.packet.ServiceForceShutdownPacket;
import de.cookiemc.driver.services.packet.ServiceRequestShutdownPacket;
import de.cookiemc.driver.networking.protocol.packets.IPacket;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.impl.DefaultServiceManager;
import de.cookiemc.driver.networking.IHandlerNetworkExecutor;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;

import de.cookiemc.remote.Remote;
import org.jetbrains.annotations.NotNull;

public class RemoteServiceManager extends DefaultServiceManager {

    public RemoteServiceManager() {
        IHandlerNetworkExecutor executor = CloudDriver.getInstance().getNetworkExecutor();
        executor.registerPacketHandler((PacketHandler<ServiceForceShutdownPacket>) (ctx, packet) -> {
            if (packet.getService().equalsIgnoreCase(Remote.getInstance().thisSidesClusterParticipant().getName())) {
                Remote.getInstance().shutdown();
            }
        });
    }

    @EventListener
    public void handleAdd(ServiceRegisterEvent event) {
        ICloudServer server = event.getCloudServer();
        this.registerService(server);
    }

    @EventListener
    public void handleRemove(ServiceUnregisterEvent event) {
        ICloudServer server = this.getService(event.getService());
        if (server == null) {
            return;
        }
        this.unregisterService(server);
    }

    @EventListener
    public void handleUpdate(ServiceUpdateEvent event) {
        ICloudServer server = event.getService();

        this.updateServerInternally(server);
    }


    @Override
    public Task<ICloudServer> startService(@NotNull ICloudServer service) {

        new NodeRequestServerStartPacket(service, false).publishTo(service.getRunningNodeName());
        return Task.newInstance(service);
    }

    @Override
    public ICloudServer thisServiceOrNull() {
        return getAllCachedServices().stream().filter(s -> s.getName().equalsIgnoreCase(Remote.getInstance().getProperty().getName())).findFirst().orElse(null);
    }

    @Override
    public Task<ICloudServer> thisService() {
        return Task.callAsync(() -> getAllCachedServices().stream().filter(s -> s.getName().equalsIgnoreCase(Remote.getInstance().getProperty().getName())).findFirst().orElse(null));
    }

    @Override
    public void shutdownService(ICloudServer service) {
        CloudDriver.getInstance().getNetworkExecutor().sendPacket(new ServiceRequestShutdownPacket(service.getName()));
    }


    @Override
    public void updateService(@NotNull ICloudServer service) {
        this.updateServerInternally(service);
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new ServiceUpdateEvent(service));
    }

    @Override
    public void sendPacketToService(ICloudServer service, @NotNull IPacket packet) {
        if (service.getName().equalsIgnoreCase(Remote.getInstance().thisSidesClusterParticipant().getName())) {
            CloudDriver.getInstance().getNetworkExecutor().handlePacket(null, packet);
            return;
        }
        Remote.getInstance().getNetworkExecutor().sendPacket(new RedirectPacket(service.getName(), packet));
    }

}
