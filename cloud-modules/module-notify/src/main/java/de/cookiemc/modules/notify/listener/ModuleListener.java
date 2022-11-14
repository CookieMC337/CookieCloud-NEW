package de.cookiemc.modules.notify.listener;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.event.EventListener;
import de.cookiemc.driver.event.defaults.server.ServiceReadyEvent;
import de.cookiemc.driver.event.defaults.server.ServiceRegisterEvent;
import de.cookiemc.driver.event.defaults.server.ServiceUnregisterEvent;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.player.ICloudPlayerManager;
import de.cookiemc.driver.player.executor.PlayerExecutor;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.modules.notify.NotifyModule;
import de.cookiemc.modules.notify.config.NotifyConfiguration;

public class ModuleListener {

    @EventListener
    public void handleAdd(ServiceRegisterEvent event) {
        ICloudServer cloudServer = event.getCloudServer();

        this.notifyNetwork(0, cloudServer);
    }


    @EventListener
    public void handleRemove(ServiceUnregisterEvent event) {
        ICloudServer cloudServer = event.getCloudServer();
        if (cloudServer == null) {
            return;
        }
        this.notifyNetwork(1, cloudServer);
    }

    @EventListener
    public void handleReady(ServiceReadyEvent event) {
        ICloudServer cloudServer = event.getCloudServer();
        if (cloudServer == null) {
            return;
        }
        this.notifyNetwork(2, cloudServer);
    }

    /**
     * Notifies every player on the network that has not disabled
     * receiving notification messages from this module
     *
     * @param state       the state of message (0 = start, 1 = stop, 2 = ready)
     * @param ICloudServer the server to get info about
     */
    public void notifyNetwork(int state, ICloudServer ICloudServer) {
        NotifyConfiguration config = NotifyModule.getInstance().getConfiguration();

        //if module is disabled just ignore execution
        if (!config.isEnabled()) {
            return;
        }


        String message = "";
        if (state == 2 && !config.isShowReadyMessage()) { //if state is READY but config has disabled this extra message -> ignore execution
            return;
        }

        switch (state) {
            case 0: //starting of server
                message = config.getMessages().getStartMessage();
                break;
            case 1: //stopping of server
                message = config.getMessages().getStopMessage();
                break;

            case 2: //ready of server
                message = config.getMessages().getReadyMessage();
                break;
        }

        //applying placeholders
        message = ICloudServer.replacePlaceHolders(message);


        //iterating through all players
        for (ICloudPlayer player : CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getAllCachedCloudPlayers()) {
            if (!config.getEnabledNotifications().contains(player.getUniqueId())) {
                continue; //player has disabled messages or is not empowered to receive some
            }
            PlayerExecutor executor = PlayerExecutor.forPlayer(player);

            //sending message to player
            executor.sendMessage(message.replace("%prefix%", config.getMessages().getPrefix()));
        }

        if (config.isDisplayInConsole()) {
            if (config.isDisplayPrefixInConsole()) {
                message = message.replace("%prefix%", config.getMessages().getPrefix());
            } else {
                message = message.replace("%prefix%", "");
            }
            message = message.trim();
            CloudDriver.getInstance().getLogger().info(message);
        }
    }
}
