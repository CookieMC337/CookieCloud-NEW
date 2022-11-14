package de.cookiemc.modules.sign.cloud.listener;

import de.cookiemc.driver.event.EventListener;
import de.cookiemc.driver.event.defaults.server.ServiceReadyEvent;
import de.cookiemc.modules.sign.api.CloudSignAPI;

public class ModuleServiceReadyListener {

    @EventListener
    public void handle(ServiceReadyEvent event) {
        CloudSignAPI.getInstance().getSignManager().update();
        CloudSignAPI.getInstance().publishConfiguration();
    }
}
