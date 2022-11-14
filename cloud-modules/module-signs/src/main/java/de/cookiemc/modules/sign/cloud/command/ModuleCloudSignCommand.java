package de.cookiemc.modules.sign.cloud.command;

import de.cookiemc.driver.commands.context.CommandContext;
import de.cookiemc.driver.commands.data.Command;
import de.cookiemc.driver.commands.data.enums.CommandScope;
import de.cookiemc.driver.commands.parameter.CommandArguments;
import de.cookiemc.modules.sign.api.CloudSignAPI;

public class ModuleCloudSignCommand {

    @Command(
            label = "reloadSigns",
            permission = "cloud.command.sign",
            desc = "Reloads the SignModule!",
            scope = CommandScope.CONSOLE
    )
    public void reload(CommandContext<?> ctx, CommandArguments args) {

        CloudSignAPI.getInstance().publishConfiguration();
        CloudSignAPI.getInstance().getSignManager().update();
        ctx.sendMessage("Reloaded!");
    }
}
