package de.cookiemc.common.logging.internal.factory;

import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.logging.LoggerFactory;
import de.cookiemc.common.logging.LogLevel;
import de.cookiemc.common.logging.internal.WrappedSlf4jLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Slf4jLoggerFactory implements LoggerFactory {

	@Nonnull
	@Override
	public Logger forName(@Nullable String name) {
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(name == null ? "Logger" : name);
		return logger instanceof Logger ? (Logger) logger : new WrappedSlf4jLogger(logger);
	}

	@Override
	public void setDefaultLevel(@Nonnull LogLevel level) {
	}

}
