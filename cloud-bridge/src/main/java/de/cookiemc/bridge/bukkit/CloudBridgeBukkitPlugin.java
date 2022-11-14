package de.cookiemc.bridge.bukkit;

import de.cookiemc.bridge.CloudBridge;
import de.cookiemc.bridge.IBridgePlugin;
import de.cookiemc.bridge.bukkit.listener.BukkitPlayerCommandListener;
import de.cookiemc.document.DocumentFactory;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.IServiceCycleData;
import de.cookiemc.driver.services.impl.DefaultServiceCycleData;
import de.cookiemc.driver.services.utils.RemoteIdentity;
import de.cookiemc.driver.services.utils.ServiceProcessType;
import de.cookiemc.driver.services.utils.ServiceState;
import de.cookiemc.driver.services.utils.ServiceVisibility;
import de.cookiemc.remote.Remote;
import de.cookiemc.remote.adapter.IBridgeExtension;
import de.cookiemc.remote.adapter.IBridgeProxyExtension;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This Implemented BridgePlugin on Spigot-Side
 *
 * @author CookieMC337
 * @since SNAPSHOT-1.3
 */
public class CloudBridgeBukkitPlugin extends JavaPlugin implements IBridgePlugin, IBridgeExtension {

    @Override
    public void onLoad() {
        RemoteIdentity identity = getIdentity();
        if (identity.getProcessType() == ServiceProcessType.BRIDGE_PLUGIN) {
            Remote remote = new Remote(identity);
            remote.nexCacheUpdate().syncUninterruptedly().get();
        }

        CloudBridge.init();
        CloudBridge.setRemoteExtension(this);
    }

    @Override
    public void onEnable() {
        this.bootstrap();

        Bukkit.getPluginManager().registerEvents(new BukkitPlayerCommandListener(this), this);
        CloudBridge.startUpdateTask();
    }

    @Override
    public void onDisable() {
        ICloudServer cloudServer = (ICloudServer) CloudDriver.getInstance().thisSidesClusterParticipant();
        cloudServer.setServiceState(ServiceState.STOPPING);
        cloudServer.setServiceVisibility(ServiceVisibility.INVISIBLE);
        cloudServer.update();
    }

    @Override
    public void shutdown() {
        Bukkit.getScheduler().runTask(this, Bukkit::shutdown);
    }

    @Override
    public IBridgeProxyExtension asProxyExtension() throws ClassCastException {
        throw new ClassCastException("BukkitBridge != ProxyBridge");
    }

    @Override
    public void executeCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public IServiceCycleData createCycleData() {
        return new DefaultServiceCycleData(DocumentFactory.newJsonDocument(
                "version", Bukkit.getVersion(),
                "gameVersion", Bukkit.getBukkitVersion(),
                "pluginChannels", Bukkit.getMessenger().getOutgoingChannels(),
                "onlineCount", Bukkit.getOnlinePlayers().size(),
                "plugins", Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(p -> p.getDescription().getName()).collect(Collectors.toList()),
                "favicon", Bukkit.getServerIcon() == null ? null : Bukkit.getServerIcon().toString(),
                "playerLimit", Bukkit.getMaxPlayers()
        ));
    }
}
