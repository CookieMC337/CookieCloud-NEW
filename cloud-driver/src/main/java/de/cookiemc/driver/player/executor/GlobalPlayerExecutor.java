package de.cookiemc.driver.player.executor;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.component.Component;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.player.ICloudPlayerManager;
import de.cookiemc.driver.services.ICloudServer;

import java.util.UUID;

public class GlobalPlayerExecutor implements PlayerExecutor {

    public static final GlobalPlayerExecutor INSTANCE = new GlobalPlayerExecutor();
    private final static UUID GLOBAL_UUID = new UUID(0L, 0L);

    @Override
    public UUID getExecutorUniqueId() {
        return GLOBAL_UUID;
    }

    @Override
    public void sendMessage(String message) {
        for (ICloudPlayer player : CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getAllCachedCloudPlayers()) {
            PlayerExecutor.forPlayer(player).sendMessage(message);
        }
    }

    @Override
    public void sendMessage(Component component) {
        for (ICloudPlayer player : CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getAllCachedCloudPlayers()) {
            PlayerExecutor.forPlayer(player).sendMessage(component);
        }
    }

    @Override
    public void disconnect(String reason) {
        for (ICloudPlayer player : CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getAllCachedCloudPlayers()) {
            PlayerExecutor.forPlayer(player).disconnect(reason);
        }
    }

    @Override
    public void setTabList(String header, String footer) {
        for (ICloudPlayer player : CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getAllCachedCloudPlayers()) {
            PlayerExecutor.forPlayer(player).setTabList(header, footer);
        }
    }

    @Override
    public void connect(ICloudServer server) {
        for (ICloudPlayer player : CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getAllCachedCloudPlayers()) {
            PlayerExecutor.forPlayer(player).connect(server);
        }
    }
}
