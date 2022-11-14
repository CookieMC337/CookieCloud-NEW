package de.cookiemc.driver.node.packet;

import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.driver.node.data.DefaultNodeCycleData;
import de.cookiemc.driver.node.data.INodeCycleData;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NodeCycleDataPacket extends AbstractPacket {

    private String nodeName;
    private INodeCycleData data;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {

        switch (state) {

            case READ:
                nodeName = buf.readString();
                data = buf.readObject(DefaultNodeCycleData.class);
                break;

            case WRITE:
                buf.writeString(nodeName);
                buf.writeObject(data);
                break;
        }
    }
}
