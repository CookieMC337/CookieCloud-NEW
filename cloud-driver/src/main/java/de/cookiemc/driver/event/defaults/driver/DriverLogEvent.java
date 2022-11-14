package de.cookiemc.driver.event.defaults.driver;

import de.cookiemc.common.logging.handler.LogEntry;
import de.cookiemc.driver.event.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DriverLogEvent implements CloudEvent {

    private LogEntry entry;
}
