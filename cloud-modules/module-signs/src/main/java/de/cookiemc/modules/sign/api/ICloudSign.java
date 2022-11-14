package de.cookiemc.modules.sign.api;

import de.cookiemc.common.identification.ImmutableUUIDHolder;
import de.cookiemc.common.location.ModifiableLocation;
import de.cookiemc.common.task.Task;
import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import de.cookiemc.driver.services.task.IServiceTask;

import java.util.UUID;

public interface ICloudSign extends ImmutableUUIDHolder, IBufferObject {

    UUID getUniqueId();

    String getTaskName();

    Task<IServiceTask> findTaskAsync();

    IServiceTask findTask();

    ModifiableLocation<Integer> getLocation();
}
