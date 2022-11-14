package de.cookiemc.common.logging.handler;

import de.cookiemc.common.logging.LogLevel;

import javax.annotation.Nonnull;


public class HandledSyncLogger extends HandledLogger {

	public HandledSyncLogger(@Nonnull LogLevel initialLevel) {
		super(initialLevel);
	}

	@Override
	protected void log0(@Nonnull LogEntry entry) {
		logNow(entry);
	}
}
