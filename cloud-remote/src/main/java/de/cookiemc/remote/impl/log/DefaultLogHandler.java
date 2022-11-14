package de.cookiemc.remote.impl.log;

import de.cookiemc.common.logging.LogLevel;
import de.cookiemc.common.logging.formatter.UncoloredMessageFormatter;
import de.cookiemc.common.logging.handler.LogEntry;
import de.cookiemc.common.logging.handler.LogHandler;

import javax.annotation.Nonnull;
import java.io.PrintStream;


public class DefaultLogHandler implements LogHandler {

	private final PrintStream out = System.out, err = System.err;

	@Override
	public void handle(@Nonnull LogEntry entry) {
		PrintStream stream = entry.getLevel() == LogLevel.ERROR ? err : out;
		stream.println(UncoloredMessageFormatter.format(entry));
	}

}
