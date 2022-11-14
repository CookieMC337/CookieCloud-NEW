package de.cookiemc.common.logging.internal.factory;

import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.logging.LoggerFactory;
import de.cookiemc.common.logging.LogLevel;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


@AllArgsConstructor
public class ConstantLoggerFactory implements LoggerFactory {

	protected final Logger logger;

	@Nonnull
	@Override
	public Logger forName(@Nullable String name) {
		return logger;
	}

	@Override
	public void setDefaultLevel(@Nonnull LogLevel level) {
		logger.setMinLevel(level);
	}

}
