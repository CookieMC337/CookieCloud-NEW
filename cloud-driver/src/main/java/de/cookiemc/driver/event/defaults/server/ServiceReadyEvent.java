package de.cookiemc.driver.event.defaults.server;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.event.CloudEvent;
import de.cookiemc.driver.event.ProtocolTansferableEvent;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.ICloudServiceManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * This {@link CloudEvent} signals that a certain {@link ICloudServer}
 * is already registered and is now connected and authenticated and ready to use
 *
 * @author CookieMC337
 * @see ServiceUpdateEvent
 * @see ServiceUnregisterEvent
 * @since SNAPSHOT-1.0
 */
@AllArgsConstructor @Getter
@NoArgsConstructor
public class ServiceReadyEvent implements ProtocolTansferableEvent {

    /**
     * The server that is now ready
     */
    private String name;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        switch (state) {
            case READ:
                name = buf.readString();
                break;
            case WRITE:
                buf.writeString(name);
                break;
        }
    }

    public ICloudServer getCloudServer() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getService(this.name);
    }

    public Task<ICloudServer> getCloudServerAsync() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getServiceAsync(this.name);
    }
}
