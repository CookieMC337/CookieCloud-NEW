package de.cookiemc.bridge;

import de.cookiemc.bridge.universal.UniversalBridgeCommandHandler;
import de.cookiemc.bridge.universal.UniversalBridgeUpdateHandler;
import de.cookiemc.bridge.proxy.UniversalProxyPlayerExecutorHandler;
import de.cookiemc.common.scheduler.Scheduler;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.utils.SpecificDriverEnvironment;
import de.cookiemc.remote.Remote;
import de.cookiemc.remote.adapter.IBridgeExtension;
import lombok.Getter;
import lombok.Setter;

import static de.cookiemc.driver.CloudDriver.SERVER_PUBLISH_INTERVAL;

public class CloudBridge {

    /**
     * The remote extension for bridge instance
     * @see IBridgeExtension
     */
    @Setter @Getter
    private static IBridgeExtension remoteExtension;

    /**
     * The bridge plugin implementation
     * @see IBridgePlugin
     */
    @Setter @Getter
    private static IBridgePlugin plugin;

    /**
     * Init the whole bridge and loads different universal
     * methods to not double implement those methods
     */
    public static void init() {

        //only needed on proxy side
        if (Remote.getInstance().getProperty().getVersionType().getEnvironment() == SpecificDriverEnvironment.PROXY) {
            UniversalProxyPlayerExecutorHandler.init();
        }

        //registering handler
        Remote.getInstance().getNetworkExecutor().registerPacketHandler(new UniversalBridgeUpdateHandler());
        Remote.getInstance().getNetworkExecutor().registerPacketHandler(new UniversalBridgeCommandHandler());
    }

    /**
     * Updates the current server info of
     * the server this bridge is running on
     */
    public static void updateServiceInfo() {
        ICloudServer server = Remote.getInstance().thisSidesClusterParticipant();

        if (remoteExtension == null || server == null) {
            return;
        }
        server.setLastCycleData(remoteExtension.createCycleData());
        server.update();
    }

    /**
     * Starts the service updating
     * task that updates the own server info every interval
     * that is defined in {@link CloudDriver}
     */
    public static void startUpdateTask() {
        //service cycle updateTask task
        Scheduler
                .runTimeScheduler()
                .scheduleRepeatingTaskAsync(
                        CloudBridge::updateServiceInfo,
                        SERVER_PUBLISH_INTERVAL,
                        SERVER_PUBLISH_INTERVAL
                );
    }
}
