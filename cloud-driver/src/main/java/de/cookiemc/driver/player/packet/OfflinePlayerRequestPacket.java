package de.cookiemc.driver.player.packet;

import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.driver.player.CloudOfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

import static de.cookiemc.driver.player.packet.OfflinePlayerRequestPacket.PayLoad.*;


public class OfflinePlayerRequestPacket extends AbstractPacket {

    public OfflinePlayerRequestPacket() {
        super(buf -> buf.writeEnum(GET_ALL));
    }

    public OfflinePlayerRequestPacket(CloudOfflinePlayer savedPlayer) {
        super(buf -> buf.writeEnum(SAVE_PLAYER).writeObject(savedPlayer));
    }

    public OfflinePlayerRequestPacket(String name) {
        super(buf -> buf.writeEnum(GET_BY_NAME).writeString(name));
    }

    public OfflinePlayerRequestPacket(UUID uniqueID) {
        super(buf -> buf.writeEnum(GET_BY_NAME).writeUniqueId(uniqueID));
    }

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
    }

    public enum PayLoad {

        SAVE_PLAYER,

        GET_ALL,

        GET_BY_NAME,

        GET_BY_UUID

    }
}
