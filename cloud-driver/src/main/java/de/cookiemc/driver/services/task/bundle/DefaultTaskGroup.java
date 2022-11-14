package de.cookiemc.driver.services.task.bundle;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.services.task.ICloudServiceTaskManager;
import de.cookiemc.driver.services.task.TaskDownloadEntry;
import de.cookiemc.driver.services.task.IServiceTask;
import de.cookiemc.driver.services.template.ITemplate;
import de.cookiemc.driver.services.template.def.CloudTemplate;
import de.cookiemc.driver.services.utils.ServiceShutdownBehaviour;
import de.cookiemc.driver.services.utils.SpecificDriverEnvironment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultTaskGroup implements ITaskGroup {

    private String name;
    private SpecificDriverEnvironment environment;
    private ServiceShutdownBehaviour shutdownBehaviour;

    private String[] javaArguments;
    private Collection<TaskDownloadEntry> downloadEntries;
    private Collection<CloudTemplate> templates;

    public @NotNull Collection<ITemplate> getTemplates() {
        return new ArrayList<>(templates);
    }

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        switch (state) {
            case WRITE:
                buf.writeString(name);
                buf.writeEnum(environment);
                buf.writeEnum(shutdownBehaviour);

                buf.writeOptionalStringArray(javaArguments);
                buf.writeObjectCollection(downloadEntries);
                buf.writeObjectCollection(templates);
                break;

            case READ:
                name = buf.readString();
                environment = buf.readEnum(SpecificDriverEnvironment.class);
                shutdownBehaviour = buf.readEnum(ServiceShutdownBehaviour.class);
                javaArguments = buf.readOptionalStringArray();
                downloadEntries = buf.readObjectCollection(TaskDownloadEntry.class);
                templates = buf.readObjectCollection(CloudTemplate.class);

                break;
        }
    }

    @Override
    public @NotNull Collection<IServiceTask> getChildren() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).getAllCachedTasks().stream().filter(c -> c.getTaskGroup().getName().equals(this.name)).collect(Collectors.toList());
    }
}
