package de.cookiemc.modules.perms.ingame.spigot;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.permission.PermissionManager;
import de.cookiemc.modules.perms.ingame.RemotePermissionManager;
import de.cookiemc.modules.perms.ingame.spigot.listener.BukkitCloudPermsListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitCloudPermsPlugin extends JavaPlugin {


    @Override
    public void onEnable() {
        this.initInjections();
        this.initListeners();
    }


    private void initInjections() {
        CloudDriver.getInstance().getProviderRegistry().setProvider(PermissionManager.class, new RemotePermissionManager());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            BukkitCloudPermsHelper.injectPermissible(onlinePlayer);
        }
    }

    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new BukkitCloudPermsListener(), this);
    }
}
