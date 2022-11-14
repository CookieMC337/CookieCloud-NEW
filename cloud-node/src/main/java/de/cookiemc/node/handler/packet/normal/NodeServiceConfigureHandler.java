package de.cookiemc.node.handler.packet.normal;

import de.cookiemc.document.Document;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.services.packet.ServiceConfigPacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.services.IFutureCloudServer;
import de.cookiemc.driver.services.task.ICloudServiceTaskManager;
import de.cookiemc.driver.services.task.IServiceTask;
import de.cookiemc.driver.services.template.ITemplate;
import de.cookiemc.driver.services.utils.version.ServiceVersion;

import java.util.Collection;
import java.util.UUID;

public class NodeServiceConfigureHandler implements PacketHandler<ServiceConfigPacket> {

    @Override
    public void handle(PacketChannel wrapper, ServiceConfigPacket packet) {

        IServiceTask serviceTask = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).getTaskOrNull(packet.getServiceTask());
        UUID uniqueId = packet.getUniqueId();
        Document properties = packet.getProperties();
        Collection<ITemplate> templates = packet.getTemplates();

        String motd = packet.getMotd();
        String node = packet.getNode();
        ServiceVersion version = packet.getVersion();

        int port = packet.getPort();
        int memory = packet.getMemory();
        int maxPlayers = packet.getMaxPlayers();
        boolean ignoreOfLimit = packet.isIgnoreOfLimit();

        IFutureCloudServer configurableService = serviceTask.configureFutureService();
        if (ignoreOfLimit) configurableService.ignoreIfLimitOfServicesReached();

        configurableService
                .uniqueId(uniqueId)
                .node(node)
                .motd(motd)
                .version(version)
                .port(port)
                .memory(memory)
                .maxPlayers(maxPlayers)
                .templates(templates.toArray(new ITemplate[0]))
                .properties(properties)
                .start();
    }
}
