package de.cookiemc.bridge.proxy;

import de.cookiemc.bridge.CloudBridge;
import de.cookiemc.common.logging.Logger;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.common.CloudMessages;
import de.cookiemc.driver.event.EventListener;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.driver.DriverCacheUpdateEvent;
import de.cookiemc.driver.event.defaults.server.ServiceRegisterEvent;
import de.cookiemc.driver.event.defaults.server.ServiceUnregisterEvent;
import de.cookiemc.driver.event.defaults.task.TaskMaintenanceChangeEvent;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.player.executor.PlayerExecutor;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.ICloudServiceManager;
import de.cookiemc.driver.services.task.IServiceTask;
import de.cookiemc.driver.storage.INetworkDocumentStorage;
import de.cookiemc.remote.adapter.IBridgeProxyExtension;
import net.md_5.bungee.api.ProxyServer;

import java.util.List;

public class UniversalProxyPlayerExecutorHandler {

    /**
     * Initializes this helper class
     */
    public static void init() {
        UniversalProxyPlayerExecutorHandler handler = new UniversalProxyPlayerExecutorHandler();

        handler.preCacheServices();
        CloudDriver
                .getInstance()
                .getProviderRegistry()
                .get(IEventManager.class)
                .ifPresent(eventManager -> {
                    eventManager.registerListener(handler);
                });
    }

    /**
     * Private constructor because utility class
     */
    private UniversalProxyPlayerExecutorHandler() {
    }



    /**
     * Pre caches every loaded {@link ICloudServer}
     * before this plugin is even loaded and ready to use
     */
    private void preCacheServices() {
        CloudDriver
                .getInstance()
                .getProviderRegistry()
                .get(ICloudServiceManager.class)
                .ifPresent(sm -> {
                    sm.getAllCachedServices()
                            .stream()
                            .filter(t -> t.getTask().getVersion().isProxy())
                            .forEach(s -> {
                                IBridgeProxyExtension adapter = CloudBridge.getRemoteExtension().asProxyExtension();
                                if (adapter != null) {
                                    adapter.unregisterService(s);
                                }
                            });
                });
    }

    @EventListener
    public void handle(ServiceRegisterEvent event) {
        ICloudServer cloudServer = event.getCloudServer();
        cloudServer.getTaskAsync()
                .ifPresentOrElse(task -> {
                    if (!task.getVersion().isProxy()) {
                        IBridgeProxyExtension adapter = CloudBridge.getRemoteExtension().asProxyExtension();
                        if (adapter != null) {
                            adapter.registerService(cloudServer);
                        }
                    }
                }, () -> {
                    Logger.constantInstance().error("Task for Server '{}' is null!", cloudServer.getName());
                });

    }

    @EventListener
    public void handle(ServiceUnregisterEvent event) {
        IBridgeProxyExtension adapter = CloudBridge.getRemoteExtension().asProxyExtension();
        if (adapter != null) {
            adapter.registerService(event.getCloudServer());
        }
    }

    @EventListener
    public void handle(DriverCacheUpdateEvent event) {
        ProxyServer.getInstance().getServers().clear();
        for (ICloudServer service : CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getAllCachedServices()) {
            if (!service.getTask().getVersion().isProxy()) {
                IBridgeProxyExtension adapter = CloudBridge.getRemoteExtension().asProxyExtension();
                if (adapter != null) {
                    adapter.registerService(service);
                }
            }
        }
    }


    //LISTENERS

    @EventListener
    public void handle(TaskMaintenanceChangeEvent event) {
        IServiceTask task = event.getTask();
        CloudMessages cloudMessages = CloudMessages.retrieveFromStorage();

        ICloudServer thisService = CloudDriver.getInstance()
                .getProviderRegistry()
                .getUnchecked(ICloudServiceManager.class)
                .thisServiceOrNull();

        if (event.isNewMaintenanceValue() && task.getName().equalsIgnoreCase(thisService.getTask().getName())) {

            List<String> whitelistedPlayers = CloudDriver.getInstance().getProviderRegistry().getUnchecked(INetworkDocumentStorage.class).getBundle("cloud::whitelist").toInstances(String.class);
            for (ICloudPlayer cp : thisService.getOnlinePlayers()) {
                if (whitelistedPlayers.contains(cp.getName())) {
                    PlayerExecutor.forPlayer(cp).sendMessage(cloudMessages.getMaintenanceKickByPassedMessage());
                    continue;
                }
                PlayerExecutor.forPlayer(cp).disconnect(cloudMessages.getNetworkCurrentlyInMaintenance());
            }

        }
    }
}
