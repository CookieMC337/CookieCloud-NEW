package de.cookiemc.modules.notify.command;

import de.cookiemc.driver.commands.context.defaults.PlayerCommandContext;
import de.cookiemc.driver.commands.data.Command;
import de.cookiemc.driver.commands.data.enums.CommandScope;
import de.cookiemc.driver.commands.parameter.CommandArguments;
import de.cookiemc.modules.notify.NotifyModule;
import de.cookiemc.modules.notify.config.NotifyConfiguration;

@Command(
        label = "notify",
        desc = "Manages the notify module",

        scope = CommandScope.INGAME_HOSTED_ON_CLOUD_SIDE
)
public class NotifyCommand {


    @Command(
            parent = "notify",
            label = "toggle",
            desc = "Toggles receiving notifications",
            permission = "cloud.modules.notify.command.use"
    )
    public void toggleCommand(PlayerCommandContext ctx, CommandArguments args) {

        ctx.getPlayerAsync()
                .onTaskSucess(player -> {

                    NotifyConfiguration configuration = NotifyModule.getInstance().getConfiguration();
                    if (configuration.getEnabledNotifications().contains(player.getUniqueId())) {
                        //has enabled notify ==> disabling

                        configuration.getEnabledNotifications().remove(player.getUniqueId());
                        player.sendMessage("§7You will §cno longer §7receive notifications§8!");
                    } else {
                        //has disabled notify ==> enabling

                        configuration.getEnabledNotifications().add(player.getUniqueId());
                        player.sendMessage("§7You will §anow §7receive notifications§8!");
                    }

                    NotifyModule.getInstance().getController().getConfig().set(configuration);
                    NotifyModule.getInstance().getController().getConfig().save();
                });

    }


}
