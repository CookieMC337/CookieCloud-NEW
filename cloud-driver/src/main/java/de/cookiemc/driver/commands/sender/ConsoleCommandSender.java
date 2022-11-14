package de.cookiemc.driver.commands.sender;

import de.cookiemc.common.logging.Logger;
import de.cookiemc.driver.console.Console;

import javax.annotation.Nonnull;


public interface ConsoleCommandSender extends CommandSender {

	@Nonnull
    Console getConsole();

	@Nonnull
    Logger getLogger();

}
