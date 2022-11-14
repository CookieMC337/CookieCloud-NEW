package de.cookiemc.modules.proxy.command;

import de.cookiemc.driver.commands.context.CommandContext;
import de.cookiemc.driver.commands.data.Command;
import de.cookiemc.driver.commands.data.enums.CommandScope;
import de.cookiemc.driver.commands.parameter.CommandArguments;
import de.cookiemc.modules.proxy.ProxyModule;

@Command(
        label = "proxy",
        scope = CommandScope.CONSOLE_AND_INGAME,
        desc = "Manages the proxy module",
        permission = "cloud.command.proxy",
        invalidUsageIfEmptyInput = true,
        autoHelpAliases = {"help", "?"}
)
public class ProxyCommand {

    @Command(
            parent = "proxy",
            label = "rl",
            desc = "Reloads the proxy module",
            scope = CommandScope.CONSOLE_AND_INGAME
    )
    public void executeReload(CommandContext<?> ctx, CommandArguments args) {
        ProxyModule.getInstance().loadConfig();
        ProxyModule.getInstance().updateMotd();
        ProxyModule.getInstance().updateTabList();
        ctx.sendMessage("Updated Module!");
    }
}
