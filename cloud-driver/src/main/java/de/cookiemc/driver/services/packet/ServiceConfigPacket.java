package de.cookiemc.driver.services.packet;

import de.cookiemc.document.Document;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.driver.services.template.ITemplate;
import de.cookiemc.driver.services.template.def.CloudTemplate;
import de.cookiemc.driver.services.utils.version.ServiceVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ServiceConfigPacket extends AbstractPacket {

    private UUID uniqueId;
    private String serviceTask;

    private int port;
    private int memory;

    private int maxPlayers;

    private boolean ignoreOfLimit;

    private String motd;

    private String node;

    private Document properties;

    private Collection<ITemplate> templates;

    private ServiceVersion version;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        if (state == BufferState.READ) {
            uniqueId = buf.readUniqueId();
            serviceTask = buf.readString();
            port = buf.readInt();
            memory = buf.readInt();
            maxPlayers = buf.readInt();
            ignoreOfLimit = buf.readBoolean();
            motd = buf.readString();
            node = buf.readString();
            properties = buf.readDocument();
            templates = buf.readWrapperObjectCollection(CloudTemplate.class);
            version = buf.readEnum(ServiceVersion.class);
        } else {
            buf.writeUniqueId(uniqueId);
            buf.writeString(serviceTask);
            buf.writeInt(port);
            buf.writeInt(memory);
            buf.writeInt(maxPlayers);
            buf.writeBoolean(ignoreOfLimit);
            buf.writeString(motd);
            buf.writeString(node);
            buf.writeDocument(properties);
            buf.writeObjectCollection(templates);
            buf.writeEnum(version);
        }
    }
}
