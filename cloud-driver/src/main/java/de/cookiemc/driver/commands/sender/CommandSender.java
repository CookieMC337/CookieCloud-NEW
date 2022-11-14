package de.cookiemc.driver.commands.sender;

import de.cookiemc.common.DriverUtility;

import javax.annotation.Nonnull;

/**
 * @see PlayerCommandSender
 * @see ConsoleCommandSender
 */
public interface CommandSender {

	default void forceMessage(@Nonnull String message) {
	}

	void sendMessage(String message);

	default void sendMessage(@Nonnull String message, Object... args) {
		sendMessage(DriverUtility.args(message, args));
	}

	boolean hasPermission(@Nonnull String permission);

	@Nonnull
	String getName();

}
