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
 * got unregistered within the cache of the current Driver Instance
 * and is now ready to work with
 *
 * @author Lystx
 * @see ServiceRegisterEvent
 * @see ServiceUpdateEvent
 * @since SNAPSHOT-1.0
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceUnregisterEvent implements ProtocolTansferableEvent {

    /**
     * The name of the service that is being unregistered
     */
    private String service;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {

        switch (state) {
            case READ:
                service = buf.readString();
                break;
            case WRITE:
                buf.writeString(service);
                break;
        }
    }

    public ICloudServer getCloudServer() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getService(this.service);
    }

    public Task<ICloudServer> getCloudServerAsync() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getServiceAsync(this.service);
    }
}
