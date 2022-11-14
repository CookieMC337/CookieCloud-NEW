package de.cookiemc.driver.commands.events;

import de.cookiemc.driver.commands.data.DriverCommand;
import de.cookiemc.driver.event.CloudEvent;
import lombok.Data;

@Data
public class CommandRegisterEvent implements CloudEvent {

    private final DriverCommand command;
}
