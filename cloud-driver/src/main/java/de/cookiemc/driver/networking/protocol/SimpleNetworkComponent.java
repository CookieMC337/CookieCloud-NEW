package de.cookiemc.driver.networking.protocol;

import de.cookiemc.common.DriverUtility;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.ConnectionType;
import de.cookiemc.driver.networking.NetworkComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class SimpleNetworkComponent extends DriverUtility implements NetworkComponent {

    /**
     * The name of this component
     */
    protected String name;

    /**
     * The custom type of this component
     */
    protected ConnectionType type;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {

        switch (state) {

            case READ:
                name = buf.readString();
                type = buf.readEnum(ConnectionType.class);
                break;

            case WRITE:
                buf.writeString(name);
                buf.writeEnum(type);
                break;
        }
    }

    @Override
    public void log(String message, Object... args) {
        CloudDriver.getInstance().logToExecutor(this, message, args);
    }
}
