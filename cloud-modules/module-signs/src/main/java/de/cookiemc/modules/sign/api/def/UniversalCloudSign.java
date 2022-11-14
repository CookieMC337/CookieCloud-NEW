package de.cookiemc.modules.sign.api.def;

import de.cookiemc.common.location.impl.DefaultLocation;
import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.services.task.ICloudServiceTaskManager;
import de.cookiemc.driver.services.task.IServiceTask;
import de.cookiemc.modules.sign.api.ICloudSign;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UniversalCloudSign implements ICloudSign {

    /**
     * The uuid of this sign
     */
    private UUID uniqueId;

    /**
     * The task for it
     */
    private String taskName;

    /**
     * The location of the sign
     */
    private DefaultLocation<Integer> location;


    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        switch (state) {
            case READ:
                this.uniqueId = buf.readUniqueId();
                this.taskName = buf.readString();
                this.location = (DefaultLocation<Integer>) buf.readLocation();
                break;
            case WRITE:
                buf.writeUniqueId(uniqueId);
                buf.writeString(taskName);
                buf.writeLocation(location);
                break;
        }
    }

    @Override
    public Task<IServiceTask> findTaskAsync() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).getTask(this.taskName);
    }

    @Override
    public IServiceTask findTask() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).getTaskOrNull(this.taskName);
    }
}
