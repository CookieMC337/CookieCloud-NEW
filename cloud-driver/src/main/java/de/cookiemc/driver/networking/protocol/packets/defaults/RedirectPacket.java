package de.cookiemc.driver.networking.protocol.packets.defaults;

import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.driver.networking.protocol.packets.IPacket;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RedirectPacket extends AbstractPacket {

    private String client;
    private IPacket packet;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {

        switch (state) {

            case READ:
                client = buf.readString();
                packet = buf.readPacket();
                break;

            case WRITE:
                buf.writeString(client);
                buf.writePacket(packet);
                break;
        }
    }
}
