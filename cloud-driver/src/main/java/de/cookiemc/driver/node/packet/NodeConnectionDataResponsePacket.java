package de.cookiemc.driver.node.packet;

import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.driver.node.INode;
import de.cookiemc.driver.node.UniversalNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NodeConnectionDataResponsePacket extends AbstractPacket {

    private String node;
    private PayLoad payLoad;
    private INode nodeInfo;


    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {

        switch (state) {

            case READ:
                node = buf.readString();
                payLoad = buf.readEnum(PayLoad.class);
                nodeInfo = buf.readObject(UniversalNode.class);
                break;

            case WRITE:
                buf.writeString(node);
                buf.writeEnum(payLoad);
                buf.writeObject(nodeInfo);
                break;
        }
    }

    public enum PayLoad {

        SUCCESS,

        WRONG_AUTH_KEY,

        ALREADY_NODE_EXISTS,

        SAME_NAME_AS_HEAD_NODE

    }
}
