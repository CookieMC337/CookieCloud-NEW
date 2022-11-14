package de.cookiemc.driver.services.task.bundle;

import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import de.cookiemc.driver.services.task.TaskDownloadEntry;
import de.cookiemc.driver.services.task.IServiceTask;
import de.cookiemc.driver.services.template.ITemplate;
import de.cookiemc.driver.services.utils.ServiceShutdownBehaviour;
import de.cookiemc.driver.services.utils.SpecificDriverEnvironment;
import de.cookiemc.driver.services.ICloudServer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * An {@link ITaskGroup} is a parent for {@link IServiceTask}s and contain options
 * that every child-task copies and forwards them to the {@link ICloudServer}s
 * that those tasks start
 *
 * @author Lystx
 * @since SNAPSHOT-1.4
 */
public interface ITaskGroup extends IBufferObject {

    /**
     * The name of this group
     */
    @NotNull
    String getName();

    /**
     * The custom java arguments
     */
    @NotNull
    String[] getJavaArguments();

    /**
     * The environment for services of this group (proxy / spigot)
     */
    @NotNull
    SpecificDriverEnvironment getEnvironment();

    /**
     * The behaviour on shutdown (static / dynamic)
     */
    @NotNull
    ServiceShutdownBehaviour getShutdownBehaviour();

    /**
     * The {@link TaskDownloadEntry}s that will be downloaded
     * once an {@link ICloudServer} starts
     *
     * @see TaskDownloadEntry
     */
    @NotNull
    Collection<TaskDownloadEntry> getDownloadEntries();

    /**
     * A {@link Collection} of all {@link ITemplate}s that
     * are by default used for {@link IServiceTask}s and their {@link ICloudServer}s
     */
    @NotNull
    Collection<ITemplate> getTemplates();

    /**
     * Retrieves the children-tasks in a {@link Collection}
     */
    @NotNull
    Collection<IServiceTask> getChildren();

}
