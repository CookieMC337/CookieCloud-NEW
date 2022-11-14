package de.cookiemc.driver.player.connection;

import de.cookiemc.http.ProtocolAddress;
import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultPlayerConnection implements PlayerConnection, IBufferObject {

    private String proxyName;
    private ProtocolAddress address;
    private int rawVersion;
    private boolean onlineMode, legacy;

    @Nonnull
    public ProtocolVersion getVersion() {
        return ProtocolVersion.getVersion(rawVersion);
    }

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buffer) throws IOException {
        switch (state) {
            case READ:
                proxyName = buffer.readString();
                address = buffer.readAddress();
                rawVersion = buffer.readInt();
                onlineMode = buffer.readBoolean();
                legacy = buffer.readBoolean();
                break;
            case WRITE:
                buffer.writeString(proxyName);
                buffer.writeAddress(address);
                buffer.writeInt(rawVersion);
                buffer.writeBoolean(onlineMode);
                buffer.writeBoolean(legacy);
                break;
        }
    }
}
