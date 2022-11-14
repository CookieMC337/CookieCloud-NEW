package de.cookiemc.driver.player.impl;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.DriverEnvironment;
import de.cookiemc.driver.event.EventListener;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.player.CloudPlayerUpdateEvent;
import de.cookiemc.driver.event.defaults.server.ServiceUnregisterEvent;
import de.cookiemc.driver.player.packet.CloudPlayerUpdatePacket;
import de.cookiemc.driver.player.CloudOfflinePlayer;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.player.ICloudPlayerManager;
import de.cookiemc.driver.networking.IHandlerNetworkExecutor;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class DefaultPlayerManager implements ICloudPlayerManager {

    protected Map<UUID, ICloudPlayer> cachedCloudPlayers = new ConcurrentHashMap<>();


    @Override
    public void setAllCachedCloudPlayers(List<ICloudPlayer> allCachedCloudPlayers) {
        Map<UUID, ICloudPlayer> cloudPlayerMap = new ConcurrentHashMap<>();
        for (ICloudPlayer cloudPlayer : allCachedCloudPlayers) {
            cloudPlayerMap.put(cloudPlayer.getUniqueId(), cloudPlayer);
        }
        this.setCachedCloudPlayers(cloudPlayerMap);

    }


    public DefaultPlayerManager() {

        IEventManager eventManager = CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class);
        IHandlerNetworkExecutor executor = CloudDriver.getInstance().getNetworkExecutor();
        if (executor == null) {
            return;
        }
        executor.registerPacketHandler((PacketHandler<CloudPlayerUpdatePacket>) (wrapper, packet) -> {

            ICloudPlayer player = packet.getPlayer();

            this.getCloudPlayer(player.getUniqueId()).ifPresent(cp -> {
                cp.copy(player);
                if (CloudDriver.getInstance().getEnvironment() == DriverEnvironment.NODE) {
                    cp.update();
                }
                eventManager.callEventGlobally(new CloudPlayerUpdateEvent(cp));
            });
        });


        eventManager.registerListener(this);

    }

    @EventListener
    public void handle(ServiceUnregisterEvent event) {

        this.cachedCloudPlayers.values().forEach(player -> {
            if (player.getProxyServer() == null || player.getProxyServer().getName().equals(event.getService())) {
                this.cachedCloudPlayers.remove(player.getUniqueId());
            }
        });
    }


    public void setCachedCloudPlayers(Map<UUID, ICloudPlayer> cachedCloudPlayers) {
        this.cachedCloudPlayers = cachedCloudPlayers;
    }

    @Override
    public @Nullable CloudOfflinePlayer getOfflinePlayerByUniqueIdBlockingOrNull(@NotNull UUID uniqueId) {
        return getOfflinePlayerByUniqueIdAsync(uniqueId).timeOut(TimeUnit.SECONDS, 12).syncUninterruptedly().orElse(null);
    }

    @Override
    public @Nullable CloudOfflinePlayer getOfflinePlayerByNameBlockingOrNull(@NotNull String name) {
        return getOfflinePlayerByNameAsync(name).syncUninterruptedly().orElse(null);
    }

    @Override
    public @NotNull Collection<CloudOfflinePlayer> getAllOfflinePlayersBlockingOrEmpty() {
        return getAllOfflinePlayersAsync().syncUninterruptedly().orElse(new ArrayList<>());
    }

    public abstract void registerCloudPlayer(@NotNull UUID uniqueID, @NotNull String username);

    public abstract void unregisterCloudPlayer(@NotNull UUID uuid, @NotNull String name);

    public abstract void updateCloudPlayer(@NotNull ICloudPlayer cloudPlayer);

    public abstract ICloudPlayer constructPlayer(@NotNull UUID uniqueId, @NotNull String name);

    @Override
    public @NotNull List<ICloudPlayer> getAllCachedCloudPlayers() {
        return Arrays.asList(this.cachedCloudPlayers.values().toArray(new ICloudPlayer[0]));
    }

    @Override
    public @NotNull Task<ICloudPlayer> getCloudPlayer(final @NotNull UUID uniqueId) {
        return Task.newInstance(this.cachedCloudPlayers.get(uniqueId));
    }

    @Override
    public @NotNull Task<ICloudPlayer> getCloudPlayer(final @NotNull String username) {
        return Task.newInstance(this.cachedCloudPlayers.values().stream().filter(it -> it.getName().equalsIgnoreCase(username)).findAny().orElse(null));
    }

    @Override
    public ICloudPlayer getCloudPlayerByNameOrNull(@NotNull String username) {
        return this.getCloudPlayer(username).orElse(null);
    }

    @Override
    public ICloudPlayer getCloudPlayerByUniqueIdOrNull(@NotNull UUID uniqueId) {
        return this.getCloudPlayer(uniqueId).orElse(null);
    }

}
