package de.cookiemc.driver.player.packet;

import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.driver.player.impl.UniversalCloudPlayer;
import de.cookiemc.driver.player.ICloudPlayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlayerUpdatePacket extends AbstractPacket {

    private ICloudPlayer player;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {

        switch (state) {

            case READ:
                player = buf.readObject(UniversalCloudPlayer.class);
                break;

            case WRITE:
                buf.writeObject(player);
                break;
        }
    }
}
