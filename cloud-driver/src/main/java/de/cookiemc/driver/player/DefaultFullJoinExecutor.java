package de.cookiemc.driver.player;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.exception.CloudException;
import de.cookiemc.driver.player.executor.PlayerExecutor;

public class DefaultFullJoinExecutor implements PlayerFullJoinExecutor {

    @Override
    public Task<Void> execute(ICloudPlayer cloudPlayer, boolean sentToHub, boolean disconnect, boolean kickPlayersOnFallbackIfLowerRank) {

        Task<Void> task = Task.empty();
        CloudDriver.getInstance().getProviderRegistry().get(PlayerFullJoinChecker.class).ifPresent(playerFullJoinExecutor -> {
            int kickedPlayers = 0;
            for (ICloudPlayer onlinePlayer : CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getAllCachedCloudPlayers()) {
                if ((kickPlayersOnFallbackIfLowerRank && (onlinePlayer.getServer() == null || onlinePlayer.getServer().getTask().getFallback().isEnabled())) || playerFullJoinExecutor.compare(cloudPlayer, onlinePlayer).equals(cloudPlayer)) {
                    PlayerExecutor playerExecutor = PlayerExecutor.forPlayer(onlinePlayer);
                    if (sentToHub) {
                        playerExecutor.sendToFallback();
                    }
                    if (disconnect) {
                        playerExecutor.disconnect("§cA player with a higher priority joined");
                    }
                    kickedPlayers += 1;
                }
            }


            if (kickedPlayers > 0) {
                task.setResult(null);
            } else {
                task.setFailure(new CloudException("No player with lower priority than self"));
            }
        });

        return task;
    }
}
