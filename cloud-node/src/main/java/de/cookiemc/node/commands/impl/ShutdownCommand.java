package de.cookiemc.node.commands.impl;


import de.cookiemc.driver.commands.data.Command;
import de.cookiemc.driver.commands.data.enums.CommandScope;
import de.cookiemc.driver.commands.context.CommandContext;
import de.cookiemc.driver.commands.parameter.CommandArguments;
import de.cookiemc.node.NodeDriver;

public class ShutdownCommand {

    @Command(
            label = "shutdown",
            desc = "Shuts down the Cloud!",
            aliases = {"exit", "end"},
            scope = CommandScope.CONSOLE_AND_INGAME,
            permission = "cloud.command.end"
    )
    public void execute(CommandContext<?> context, CommandArguments args) {
        NodeDriver.getInstance().shutdown();
    }

}
