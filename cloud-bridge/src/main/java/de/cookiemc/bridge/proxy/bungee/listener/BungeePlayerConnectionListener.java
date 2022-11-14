package de.cookiemc.bridge.proxy.bungee.listener;

import de.cookiemc.bridge.proxy.bungee.CloudBridgeBungeePlugin;
import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.player.PlayerFullJoinExecutor;
import de.cookiemc.driver.player.ICloudPlayerManager;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.ICloudServiceManager;
import de.cookiemc.driver.services.task.IServiceTask;
import de.cookiemc.driver.storage.INetworkDocumentStorage;
import de.cookiemc.driver.uuid.IdentificationCache;
import de.cookiemc.driver.uuid.PlayerLoginProcessing;
import de.cookiemc.remote.Remote;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.List;
import java.util.UUID;

public class BungeePlayerConnectionListener implements Listener {


    /**
     * The player manager instance for quicker access
     */
    private final ICloudPlayerManager playerManager;


    /**
     * The plugin instance for quicker access
     */
    private final CloudBridgeBungeePlugin cloudBridgeBungeePlugin;

    public BungeePlayerConnectionListener(CloudBridgeBungeePlugin cloudBridgeBungeePlugin) {
        this.cloudBridgeBungeePlugin = cloudBridgeBungeePlugin;
        this.playerManager = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class);
    }



    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        playerManager.unregisterCloudPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    @EventHandler
    public void handle(PreLoginEvent event) {
        event.registerIntent(cloudBridgeBungeePlugin);

        ICloudPlayer cloudPlayer = null;

        if (Remote.getInstance().getProperty().getPlayerLoginProcessing() == PlayerLoginProcessing.UUID_CACHE && CloudDriver.getInstance().getProviderRegistry().getUnchecked(IdentificationCache.class).getUUID(event.getConnection().getName()) != null) {
            String name = event.getConnection().getName();
            UUID uuid = CloudDriver.getInstance().getProviderRegistry().getUnchecked(IdentificationCache.class).getUUID(name);

            CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).registerCloudPlayer(uuid, name);
            cloudPlayer = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getCloudPlayerByUniqueIdOrNull(uuid);
        }

        ICloudServer cloudServer = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).thisServiceOrNull();
        IServiceTask serviceTask = cloudServer.getTask();

        List<String> whitelistedPlayers = CloudDriver.getInstance().getProviderRegistry().getUnchecked(INetworkDocumentStorage.class).getBundle("cloud::whitelist").toInstances(String.class);

        if (event.getConnection().getName() != null) {
            if (serviceTask.isMaintenance() && !whitelistedPlayers.contains(event.getConnection().getName()) && !(cloudPlayer != null && cloudPlayer.hasPermission("cloud.maintenance.bypass"))) {
                event.setCancelReason(new TextComponent("§cThe network is currently in maintenance!\nCome back later!"));
                event.setCancelled(true);
                event.completeIntent(cloudBridgeBungeePlugin);
                return;
            }
        }

        if (cloudServer.getOnlinePlayers().size() >= cloudServer.getMaxPlayers()) {
            if (cloudPlayer != null) {
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(PlayerFullJoinExecutor.class).execute(cloudPlayer, false, true, true); // TODO: 25.08.2022 custom enable of kick if lower rank

            }
            event.setCancelReason(new TextComponent("§cThis Proxy is currently full!"));
            event.setCancelled(true);
            event.completeIntent(cloudBridgeBungeePlugin);
            return;
        }

        Task<ICloudServer> fallback = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getFallbackAsService();

        if (fallback.isNull()) {
            event.setCancelReason(new TextComponent("§cCould not find any fallback to connect you to..."));
            event.setCancelled(true);
            event.completeIntent(cloudBridgeBungeePlugin);
            return;
        }

        event.completeIntent(cloudBridgeBungeePlugin);
    }

    @EventHandler
    public void handle(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        IdentificationCache cache = CloudDriver.getInstance().getProviderRegistry().getUnchecked(IdentificationCache.class);


        if (cache.getUUID(player.getName()) == null || !cache.getUUID(player.getName()).equals(player.getUniqueId())) {
            cache.setUUID(player.getName(), player.getUniqueId());
            cache.update();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(LoginEvent event) {
        PendingConnection c = event.getConnection();
        if (CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getCloudPlayer(c.getName()).isPresent()) {
            return;
        }
        CloudDriver.getInstance().getLogger().info("Logging in Player[uuid={}, name={}]", c.getUniqueId(), c.getName());
        playerManager.registerCloudPlayer(c.getUniqueId(), c.getName());

    }

}
