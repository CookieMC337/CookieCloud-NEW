package de.cookiemc.driver.module.packet;

import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@NoArgsConstructor
public class RemoteModuleExecutionPacket extends AbstractPacket {

    public RemoteModuleExecutionPacket(PayLoad payload) {
        super(buf -> buf.writeEnum(payload));
    }

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
    }


    public enum PayLoad {


        RESOLVE_MODULES,

        LOAD_MODULES,

        ENABLE_MODULES,

        DISABLE_MODULES,

        UNREGISTER_MODULES,

        RETRIEVE_MODULES,


        TRANSFER_MODULES,
    }
}
