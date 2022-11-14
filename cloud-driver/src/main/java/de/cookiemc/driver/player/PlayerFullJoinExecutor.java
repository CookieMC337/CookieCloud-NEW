package de.cookiemc.driver.player;

import de.cookiemc.common.task.Task;

public interface PlayerFullJoinExecutor {

    Task<Void> execute(ICloudPlayer cloudPlayer, boolean sentToHub , boolean disconnect, boolean kickPlayersOnFallbackIfLowerRank);
}
