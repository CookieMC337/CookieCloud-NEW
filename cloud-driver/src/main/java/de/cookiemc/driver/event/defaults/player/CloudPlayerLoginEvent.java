package de.cookiemc.driver.event.defaults.player;

import de.cookiemc.driver.player.ICloudPlayer;
import org.jetbrains.annotations.NotNull;

public class CloudPlayerLoginEvent extends DefaultPlayerEvent {

    public CloudPlayerLoginEvent(final @NotNull ICloudPlayer cloudPlayer) {
        super(cloudPlayer);
    }

}
