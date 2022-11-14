package de.cookiemc.common.logging.handler;

import de.cookiemc.common.logging.LogLevel;
import lombok.Data;

import java.time.Instant;


@Data
public class LogEntry {

	private final Instant timestamp;
	private final String threadName;
	private final String message;
	private final LogLevel level;
	private final Throwable exception;


	public static LogEntry forMessage(LogLevel level, String message) {
		return new LogEntry(
				Instant.now(),
				Thread.currentThread().getName(),
				message,
				level,
				null
		);
	}
}
