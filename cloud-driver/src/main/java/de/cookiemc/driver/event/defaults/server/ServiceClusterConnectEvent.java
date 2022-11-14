package de.cookiemc.driver.event.defaults.server;

import de.cookiemc.driver.event.CloudEvent;
import de.cookiemc.driver.event.ProtocolTansferableEvent;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.impl.UniversalCloudServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * This {@link CloudEvent} signals that a certain {@link de.cookiemc.driver.services.ICloudServer}
 * has connected to the cluster but is not ready to use yet
 *
 * @author CookieMC337
 * @see ServiceUpdateEvent
 * @see ServiceUnregisterEvent
 * @since SNAPSHOT-1.0
 */
@AllArgsConstructor @Getter
@NoArgsConstructor
public class ServiceClusterConnectEvent implements ProtocolTansferableEvent {

    /**
     * The server that has connected
     */
    private ICloudServer ICloudServer;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        switch (state) {
            case READ:
                ICloudServer = buf.readObject(UniversalCloudServer.class);
                break;
            case WRITE:
                buf.writeObject(ICloudServer);
                break;
        }
    }
}
