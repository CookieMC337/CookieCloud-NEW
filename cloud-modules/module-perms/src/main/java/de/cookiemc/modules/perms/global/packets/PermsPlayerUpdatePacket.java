package de.cookiemc.modules.perms.global.packets;

import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.driver.permission.PermissionPlayer;
import de.cookiemc.modules.perms.global.impl.DefaultPermissionPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PermsPlayerUpdatePacket extends AbstractPacket {

    private PermissionPlayer player;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        if (state == BufferState.READ) {
            player = buf.readObject(DefaultPermissionPlayer.class);
        } else {
            buf.writeObject(player);
        }
    }
}
