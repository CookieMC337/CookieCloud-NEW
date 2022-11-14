package de.cookiemc.modules.sign.api;

import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.utils.ServiceState;
import de.cookiemc.driver.services.utils.ServiceVisibility;

public enum SignState {

    OFFLINE,

    STARTING,

    ONLINE,

    FULL,

    MAINTENANCE;


    public static SignState ofServer(ICloudServer server) {
        if (server.getTask().isMaintenance()) {
            return MAINTENANCE;
        } else if (server.getOnlinePlayers().size() >= server.getMaxPlayers()) {
            return FULL;
        } else if (server.getServiceState() == ServiceState.ONLINE) {
            return ONLINE;
        } else if (server.getServiceState() == ServiceState.PREPARED || server.getServiceState() == ServiceState.STARTING) {
            return STARTING;
        } else if (server.getServiceVisibility() == ServiceVisibility.INVISIBLE) {
            return OFFLINE;
        } else {
            return OFFLINE;
        }
    }
}
