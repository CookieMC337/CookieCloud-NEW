package de.cookiemc.modules.hubcommand.command;

import de.cookiemc.driver.commands.context.defaults.PlayerCommandContext;
import de.cookiemc.driver.commands.data.Command;
import de.cookiemc.driver.commands.data.enums.CommandScope;
import de.cookiemc.driver.commands.parameter.CommandArguments;
import de.cookiemc.driver.common.CloudMessages;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.player.executor.PlayerExecutor;

public class HubCommand {

    @Command(
            label = "hub",
            aliases = {"l", "lobby", "leave"},
            desc = "Sends you to a fallback server!",
            scope = CommandScope.INGAME_HOSTED_ON_CLOUD_SIDE
    )
    public void lobbyCommand(PlayerCommandContext ctx, CommandArguments args) {
        ICloudPlayer player = ctx.getPlayer();
        PlayerExecutor executor = PlayerExecutor.forPlayer(player);

        player.getServerAsync()
                .onTaskFailed(e -> {
                    player.sendMessage("§cCouldn't send you to a fallback!");
                })
                .onTaskSucess(server -> {

                    if (server.isRegisteredAsFallback()) {
                        player.sendMessage(CloudMessages.retrieveFromStorage().getAlreadyOnFallbackMessage());
                        return;
                    }
                    executor.sendToFallback();
                });
    }
}
