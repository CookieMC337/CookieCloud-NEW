package de.cookiemc.driver.event.defaults.player;

import de.cookiemc.driver.event.CloudEvent;
import de.cookiemc.driver.player.ICloudPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class RemotePermissionCheckEvent implements CloudEvent {

    private final ICloudPlayer player;
    private final String permission;

    @Setter
    private boolean hasPermission;
}
