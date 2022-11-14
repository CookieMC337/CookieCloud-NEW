package de.cookiemc.bridge.proxy.bungee.listener;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.component.style.ComponentColor;
import de.cookiemc.driver.player.ICloudPlayerManager;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.IPingProperties;
import de.cookiemc.remote.Remote;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;

public class BungeeProxyPingListener implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(ProxyPingEvent event) {
        ServerPing response = event.getResponse();

        ICloudServer cloudServer = Remote.getInstance().thisSidesClusterParticipant();
        IPingProperties pingProperties = cloudServer.getPingProperties();

        int maxPlayers, onlinePlayers;
        if (pingProperties.isUsePlayerPropertiesOfService()) {
            maxPlayers = cloudServer.getMaxPlayers();
            onlinePlayers = pingProperties.isCombineAllProxiesIfProxyService() ? CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getCloudPlayerOnlineAmount() : cloudServer.getOnlinePlayers().size();
        } else {
            maxPlayers = pingProperties.getCustomMaxPlayers();
            onlinePlayers = pingProperties.getCustomOnlinePlayers();
        }

        //player info
        String[] playerInfo = pingProperties.getPlayerInfo();
        ServerPing.PlayerInfo[] info = new ServerPing.PlayerInfo[playerInfo.length];
        for (int i = 0; i < playerInfo.length; i++) {
            info[i] = new ServerPing.PlayerInfo(ComponentColor.translateAlternateColorCodes('&', playerInfo[i]), UUID.randomUUID());
        }

        //player values
        ServerPing.Players pp = response.getPlayers();

        pp.setSample(info);
        pp.setOnline(onlinePlayers);
        pp.setMax(maxPlayers);


        //server icon
        String serverIconUrl = pingProperties.getServerIconUrl();
        if (serverIconUrl != null) {
            response.setFavicon(Favicon.create(serverIconUrl));
        }

        //motd
        response.setDescriptionComponent(new TextComponent(ComponentColor.translateAlternateColorCodes('&', pingProperties.getMotd())));

        //protocol text
        String versionText = pingProperties.getVersionText();
        if (versionText != null && !versionText.trim().isEmpty()) {
            response.setVersion(new ServerPing.Protocol(ComponentColor.translateAlternateColorCodes('&', versionText), -1));
        }

        event.setResponse(response);
    }
}
