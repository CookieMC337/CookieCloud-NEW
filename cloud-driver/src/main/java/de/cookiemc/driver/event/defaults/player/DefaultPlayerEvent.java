package de.cookiemc.driver.event.defaults.player;

import de.cookiemc.driver.event.CloudEvent;
import de.cookiemc.driver.player.ICloudPlayer;
import lombok.AllArgsConstructor;

import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class DefaultPlayerEvent implements CloudEvent {

    private ICloudPlayer cloudPlayer;

}
