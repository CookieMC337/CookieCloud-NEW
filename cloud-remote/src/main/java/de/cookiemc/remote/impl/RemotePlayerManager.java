package de.cookiemc.remote.impl;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.player.CloudPlayerDisconnectEvent;
import de.cookiemc.driver.event.defaults.player.CloudPlayerLoginEvent;
import de.cookiemc.driver.player.packet.CloudPlayerDisconnectPacket;
import de.cookiemc.driver.player.packet.CloudPlayerLoginPacket;
import de.cookiemc.driver.player.packet.CloudPlayerUpdatePacket;
import de.cookiemc.driver.player.packet.OfflinePlayerRequestPacket;
import de.cookiemc.driver.player.CloudOfflinePlayer;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.player.impl.DefaultCloudOfflinePlayer;
import de.cookiemc.driver.player.impl.DefaultPlayerManager;
import de.cookiemc.driver.player.impl.UniversalCloudPlayer;
import de.cookiemc.driver.services.ICloudServiceManager;
import de.cookiemc.remote.Remote;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class RemotePlayerManager extends DefaultPlayerManager {


    @Override
    public ICloudPlayer constructPlayer(@NotNull UUID uniqueId, @NotNull String name) {
        return new UniversalCloudPlayer(uniqueId, name);
    }

    @Override
    public @NotNull Task<Collection<CloudOfflinePlayer>> getAllOfflinePlayersAsync() {
        return Task.callAsync(new Callable<Collection<CloudOfflinePlayer>>() {
            @Override
            public Collection<CloudOfflinePlayer> call() throws Exception {
                return CloudDriver.getInstance()
                        .getNetworkExecutor()
                        .getPacketChannel()
                        .prepareSingleQuery()
                        .execute(new OfflinePlayerRequestPacket())
                        .syncUninterruptedly()
                        .get()
                        .buffer()
                        .readObjectCollection(DefaultCloudOfflinePlayer.class)
                        .stream()
                        .map(c -> ((CloudOfflinePlayer) c))
                        .collect(Collectors.toList());
            }
        });
    }

    @Override
    public @NotNull Task<CloudOfflinePlayer> getOfflinePlayerByUniqueIdAsync(@NotNull UUID uniqueId) {
        return Task.callAsync(new Callable<CloudOfflinePlayer>() {
            @Override
            public CloudOfflinePlayer call() throws Exception {
                return Remote.getInstance()
                        .getNetworkExecutor()
                        .getPacketChannel()
                        .prepareSingleQuery()
                        .execute(new OfflinePlayerRequestPacket(uniqueId))
                        .syncUninterruptedly()
                        .get()
                        .buffer()
                        .readOptionalObject(DefaultCloudOfflinePlayer.class);
            }
        });
    }

    @Override
    public void saveOfflinePlayerAsync(@NotNull CloudOfflinePlayer player) {
        Task.runAsync(() -> Remote.getInstance().getNetworkExecutor().sendPacket(new OfflinePlayerRequestPacket(player)));
    }

    @Override
    public @NotNull Task<CloudOfflinePlayer> getOfflinePlayerByNameAsync(@NotNull String name) {
        return Task.callAsync(new Callable<CloudOfflinePlayer>() {
            @Override
            public CloudOfflinePlayer call() throws Exception {
                return Remote.getInstance()
                        .getNetworkExecutor()
                        .getPacketChannel()
                        .prepareSingleQuery()
                        .execute(new OfflinePlayerRequestPacket(name))
                        .syncUninterruptedly()
                        .get()
                        .buffer()
                        .readOptionalObject(DefaultCloudOfflinePlayer.class);
            }
        });
    }

    @Override
    public void registerCloudPlayer(@NotNull UUID uniqueId, @NotNull String username) {
        ICloudPlayer cloudPlayer = constructPlayer(uniqueId, username);
        cloudPlayer.setProxyServer(CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).thisServiceOrNull());

        this.cachedCloudPlayers.put(uniqueId, cloudPlayer);
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new CloudPlayerLoginEvent(cloudPlayer));
        Remote.getInstance().getNetworkExecutor().sendPacket(new CloudPlayerLoginPacket(username, uniqueId, cloudPlayer.getProxyServer().getName()));
    }

    @Override
    public void unregisterCloudPlayer(@NotNull UUID uuid, @NotNull String username) {
        if (this.getCloudPlayerByUniqueIdOrNull(uuid) == null) {
            return;
        }
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new CloudPlayerDisconnectEvent(this.cachedCloudPlayers.remove(uuid)));
        Remote.getInstance().getNetworkExecutor().sendPacket(new CloudPlayerDisconnectPacket(uuid, username));
    }

    @Override
    public void updateCloudPlayer(@NotNull ICloudPlayer cloudPlayer) {
        Remote.getInstance().getNetworkExecutor().sendPacket(new CloudPlayerUpdatePacket(cloudPlayer));
    }

}
