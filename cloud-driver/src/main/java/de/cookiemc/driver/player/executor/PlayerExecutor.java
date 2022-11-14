package de.cookiemc.driver.player.executor;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.component.Component;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.ICloudServiceManager;

import java.util.UUID;

public interface PlayerExecutor {

    public static PlayerExecutor forAll() {
        return GlobalPlayerExecutor.INSTANCE;
    }

    public static PlayerExecutor forPlayer(ICloudPlayer player) {
        return new CloudPlayerExecutor(player);
    }

    public static PlayerExecutor forUniqueId(UUID uniqueId) {
        return new UnknownExecutor(uniqueId);
    }

    /**
     * The uuid this executor is for
     */
    UUID getExecutorUniqueId();

    void sendMessage(String message);

    void sendMessage(Component component);

    void setTabList(String header, String footer);

    void disconnect(String reason);

    void connect(ICloudServer server);

    default void sendToFallback() {

        Task<ICloudServer> fallback = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getFallbackAsService();
        fallback.ifPresent(this::connect);
        fallback.ifEmpty(task -> sendMessage("§cCould not find any available fallback..."));
    }
}
