package de.cookiemc.driver.event.defaults.player;

import de.cookiemc.driver.player.ICloudPlayer;
import org.jetbrains.annotations.NotNull;

public class CloudPlayerDisconnectEvent extends DefaultPlayerEvent {

    public CloudPlayerDisconnectEvent(@NotNull ICloudPlayer cloudPlayer) {
        super(cloudPlayer);
    }

}
