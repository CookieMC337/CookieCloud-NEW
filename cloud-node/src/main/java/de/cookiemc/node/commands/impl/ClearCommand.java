package de.cookiemc.node.commands.impl;

import de.cookiemc.driver.commands.data.Command;
import de.cookiemc.driver.commands.context.CommandContext;
import de.cookiemc.driver.commands.data.enums.CommandScope;
import de.cookiemc.driver.commands.parameter.CommandArguments;
import de.cookiemc.node.NodeDriver;

public class ClearCommand {

    @Command(
            label = "clear",
            desc = "Clears the screen",
            permission = "cloud.command.clear",
            scope = CommandScope.CONSOLE_AND_INGAME
    )
    public void executeClear(CommandContext<?> context, CommandArguments args) {
        NodeDriver.getInstance().getConsole().clearScreen();
        NodeDriver.getInstance().getConsole().printHeader();
    }
}
