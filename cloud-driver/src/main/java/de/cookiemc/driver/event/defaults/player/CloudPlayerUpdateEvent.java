package de.cookiemc.driver.event.defaults.player;

import de.cookiemc.driver.player.ICloudPlayer;
import org.jetbrains.annotations.NotNull;

public class CloudPlayerUpdateEvent extends DefaultPlayerEvent {

    public CloudPlayerUpdateEvent(final @NotNull ICloudPlayer cloudPlayer) {
        super(cloudPlayer);
    }

}
