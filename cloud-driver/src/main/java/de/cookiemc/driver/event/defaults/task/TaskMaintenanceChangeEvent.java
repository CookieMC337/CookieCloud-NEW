package de.cookiemc.driver.event.defaults.task;

import de.cookiemc.driver.event.CloudEvent;
import de.cookiemc.driver.services.task.IServiceTask;
import lombok.*;

@AllArgsConstructor
@Getter
public class TaskMaintenanceChangeEvent implements CloudEvent {

    private final IServiceTask task;
    private final boolean newMaintenanceValue;

}
