package de.cookiemc.modules.perms.ingame.bungee;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.permission.PermissionManager;
import de.cookiemc.modules.perms.ingame.RemotePermissionManager;
import de.cookiemc.modules.perms.ingame.bungee.listener.BungeeCloudPermsListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCloudPermsPlugin extends Plugin {


    @Override
    public void onEnable() {
        this.initListeners();
    }

    private void initListeners() {
        CloudDriver.getInstance().getProviderRegistry().setProvider(PermissionManager.class, new RemotePermissionManager());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeCloudPermsListener());
    }
}
