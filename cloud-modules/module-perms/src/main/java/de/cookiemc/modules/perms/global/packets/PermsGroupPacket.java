package de.cookiemc.modules.perms.global.packets;

import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.driver.permission.PermissionGroup;
import de.cookiemc.modules.perms.global.impl.DefaultPermissionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PermsGroupPacket extends AbstractPacket {

    private PayLoad payLoad;
    private PermissionGroup group;

    private String name;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        switch (state) {
            case READ:
                this.payLoad = buf.readEnum(PayLoad.class);
                this.group = buf.readOptionalObject(DefaultPermissionGroup.class);
                this.name = buf.readString();
                break;
            case WRITE:
                buf.writeEnum(payLoad);
                buf.writeOptionalObject(group);
                buf.writeString(name);
                break;
        }
    }


    public enum PayLoad {

        REMOVE,

        UPDATE,

        CREATE

    }
}
