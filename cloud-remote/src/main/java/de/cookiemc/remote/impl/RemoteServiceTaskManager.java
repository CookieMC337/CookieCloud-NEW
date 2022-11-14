package de.cookiemc.remote.impl;


import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.event.EventListener;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.task.TaskUpdateEvent;
import de.cookiemc.driver.services.task.DefaultServiceTaskManager;

import de.cookiemc.driver.services.task.IServiceTask;
import org.jetbrains.annotations.NotNull;

public class RemoteServiceTaskManager extends DefaultServiceTaskManager {

    @Override
    public void updateTask(@NotNull IServiceTask task) {
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new TaskUpdateEvent(task));
    }


    @EventListener
    public void handleUpdate(TaskUpdateEvent event) {
        IServiceTask packetTask = event.getTask();
        IServiceTask task = getTaskOrNull(packetTask.getName());
        if (task != null) {
            task.copy(packetTask);
        }
    }

}
