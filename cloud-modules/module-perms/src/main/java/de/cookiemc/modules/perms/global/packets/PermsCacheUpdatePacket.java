package de.cookiemc.modules.perms.global.packets;

import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.driver.permission.PermissionGroup;
import de.cookiemc.driver.permission.PermissionPlayer;
import de.cookiemc.modules.perms.global.impl.DefaultPermissionGroup;
import de.cookiemc.modules.perms.global.impl.DefaultPermissionPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PermsCacheUpdatePacket extends AbstractPacket {

    private Collection<PermissionGroup> permissionGroups;
    private Collection<PermissionPlayer> permissionPlayers;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        switch (state) {
            case WRITE:
                buf.writeObjectCollection(permissionGroups);
                buf.writeObjectCollection(permissionPlayers);
                break;

            case READ:
                permissionGroups = buf.readWrapperObjectCollection(DefaultPermissionGroup.class);
                permissionPlayers = buf.readWrapperObjectCollection(DefaultPermissionPlayer.class);
                break;
        }
    }
}
