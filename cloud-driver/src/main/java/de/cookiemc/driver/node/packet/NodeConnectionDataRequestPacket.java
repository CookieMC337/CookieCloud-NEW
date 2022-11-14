package de.cookiemc.driver.node.packet;

import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Getter
@Setter
public class NodeConnectionDataRequestPacket extends AbstractPacket {

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
    }
}
