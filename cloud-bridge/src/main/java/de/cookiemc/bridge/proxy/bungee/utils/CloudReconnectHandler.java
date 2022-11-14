package de.cookiemc.bridge.proxy.bungee.utils;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.ICloudServiceManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CloudReconnectHandler implements ReconnectHandler {

    @Override
    public ServerInfo getServer(ProxiedPlayer player) {
        ICloudServer fallback = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getFallbackAsServiceOrNull();
        return ProxyServer.getInstance().getServerInfo(fallback == null ? "Lobby-1" : fallback.getName());
    }

    @Override
    public void setServer(ProxiedPlayer player) {
        //not needed
    }

    @Override
    public void save() {
        //not needed
    }

    @Override
    public void close() {
        //not needed
    }

}
