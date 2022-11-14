package de.cookiemc.driver.commands.context.defaults;


import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.util.DisplayFormat;
import de.cookiemc.driver.commands.context.CommandContext;
import de.cookiemc.driver.commands.sender.ConsoleCommandSender;

import java.util.UUID;

/**
 * Instance of a {@link CommandContext} but specifically for the console
 */
public class ConsoleCommandContext extends CommandContext<ConsoleCommandSender> {

    public ConsoleCommandContext(ConsoleCommandSender sender) {
        super(sender);
    }

    @Override
    public UUID getSendersUniqueId() {
        return CONSOLE_UUID;
    }

    @Override
    protected void message(String msg, ConsoleCommandSender target) {
        Logger.constantInstance().info(msg);
    }

    @Override
    public void sendDisplayFormat(DisplayFormat format, ConsoleCommandSender... receivers) {

        format.prepare();
        format.getComponents().forEach(stringBooleanPair -> Logger.constantInstance().info(stringBooleanPair.getFirst()));
    }

}
