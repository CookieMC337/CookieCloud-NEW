package de.cookiemc.driver.networking.protocol.packets.defaults;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.driver.DriverStorageUpdateEvent;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StorageUpdatePacket extends AbstractPacket {

    private StoragePayLoad payLoad;
    private Document storage;


    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {

        switch (state) {

            case READ:
                payLoad = buf.readEnum(StoragePayLoad.class);
                storage = buf.readDocument();

                CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new DriverStorageUpdateEvent());
                break;

            case WRITE:
                buf.writeEnum(payLoad);
                buf.writeDocument(storage);
                break;
        }
    }


    public enum StoragePayLoad {

        FETCH,

        UPDATE

    }
}
