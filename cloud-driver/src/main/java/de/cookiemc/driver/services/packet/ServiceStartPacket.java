package de.cookiemc.driver.services.packet;

import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.impl.UniversalCloudServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServiceStartPacket extends AbstractPacket {

    private ICloudServer cloudServer;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        switch (state) {
            case WRITE:
                buf.writeObject(cloudServer);
                break;
            case READ:
                cloudServer = buf.readObject(UniversalCloudServer.class);
                break;
        }
    }
}
