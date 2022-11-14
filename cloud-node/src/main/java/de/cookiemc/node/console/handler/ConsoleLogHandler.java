package de.cookiemc.node.console.handler;

import de.cookiemc.common.logging.handler.LogEntry;
import de.cookiemc.common.logging.handler.LogHandler;
import de.cookiemc.driver.console.Console;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

@AllArgsConstructor
public class ConsoleLogHandler implements LogHandler {

	private final Console console;

	@Override
	public void handle(@Nonnull LogEntry entry) {
		console.writeEntry(entry);
	}

}
