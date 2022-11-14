package de.cookiemc.driver.event.defaults.task;

import de.cookiemc.driver.event.ProtocolTansferableEvent;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.services.task.UniversalServiceTask;
import de.cookiemc.driver.services.task.IServiceTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateEvent implements ProtocolTansferableEvent {

    private IServiceTask task;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        if (state == BufferState.READ) {
            task = buf.readObject(UniversalServiceTask.class);
        } else {
            buf.writeObject(task);
        }
    }
}
