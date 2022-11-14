package de.cookiemc.node.service;

import de.cookiemc.common.scheduler.Scheduler;
import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.console.screen.Screen;
import de.cookiemc.driver.console.screen.ScreenManager;
import de.cookiemc.driver.event.EventListener;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.server.ServiceRegisterEvent;
import de.cookiemc.driver.event.defaults.server.ServiceUnregisterEvent;
import de.cookiemc.driver.event.defaults.server.ServiceUpdateEvent;
import de.cookiemc.driver.networking.protocol.packets.defaults.DriverUpdatePacket;
import de.cookiemc.driver.networking.protocol.packets.IPacket;
import de.cookiemc.driver.node.INode;
import de.cookiemc.driver.node.INodeManager;
import de.cookiemc.driver.node.config.ServiceCrashPrevention;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.task.IServiceTask;
import de.cookiemc.driver.services.impl.DefaultServiceManager;
import de.cookiemc.node.NodeDriver;

import de.cookiemc.node.config.MainConfiguration;
import de.cookiemc.node.service.helper.CloudServerProcessWorker;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class NodeServiceManager extends DefaultServiceManager {

    /**
     * The worker to start service
     */
    private final CloudServerProcessWorker worker;

    public NodeServiceManager() {

        this.worker = new CloudServerProcessWorker();
    }

    @Override
    public void registerService(ICloudServer service) {
        super.registerService(service);

        ScreenManager screenManager = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ScreenManager.class);
        screenManager.registerScreen(service.getName(), false);

        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new ServiceRegisterEvent(service));

        if (NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class) != null && NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).isHeadNode()) {
            DriverUpdatePacket.publishUpdate(CloudDriver.getInstance().getNetworkExecutor());
        }
    }


    @Override
    public void unregisterService(ICloudServer service) {
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new ServiceUnregisterEvent(service.getName()));
        super.unregisterService(service);

        ScreenManager screenManager = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ScreenManager.class);
        Screen screen = screenManager.getScreenByNameOrNull(service.getName());
        IServiceTask con = service.getTask();

        File parent = (con.getTaskGroup().getShutdownBehaviour().isStatic() ? NodeDriver.SERVICE_DIR_STATIC : NodeDriver.SERVICE_DIR_DYNAMIC);
        File folder = new File(parent, service.getName() + "/");

        if (!service.isReady() && CloudDriver.getInstance().isRunning()) {
            NodeDriver.getInstance().getLogger().warn("Service {} probably crashed during startup because it was not authenticated when it stopped", service.getName());

            File crashFolder = new File(NodeDriver.LOG_FOLDER, "crashes/");
            crashFolder.mkdirs();

            File specificCrashFolders = new File(crashFolder, con.getName() + "/");
            specificCrashFolders.mkdirs();

            File crashFile = new File(specificCrashFolders, service.getName() + "_" + UUID.randomUUID().toString() + ".log");

            try {
                de.cookiemc.common.misc.FileUtils.writeToFile(crashFile, screen.getAllCachedLines());
                NodeDriver.getInstance().getLogger().warn("Saving logs to identify crash under {}...", crashFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
                NodeDriver.getInstance().getLogger().warn("Couldn't save crash logs...");
            }
            ServiceCrashPrevention scp = MainConfiguration.getInstance().getServiceCrashPrevention();

            if (scp.isEnabled()) {

                NodeDriver.getInstance().getServiceQueue().getPausedGroups().add(con.getName());

                Scheduler.runTimeScheduler().scheduleDelayedTask(() -> {
                    NodeDriver.getInstance().getServiceQueue().getPausedGroups().remove(con.getName());
                    NodeDriver.getInstance().getServiceQueue().dequeue();
                }, scp.getTimeUnit().toMillis(scp.getTime()));

                NodeDriver.getInstance().getLogger().warn("Due to ServiceCrashPrevention (SCP) being enabled, starting services of ServiceTask {} is now paused for {} {}", con.getName(), scp.getTime(), scp.getTimeUnit().name().toLowerCase());
            }

        }

        if (con.getTaskGroup().getShutdownBehaviour().isStatic()) {
            //only delete cloud files
            File property = new File(folder, "property.json");
            property.delete();

            File bridgePlugin = new File(folder, "plugins/plugin.jar");
            bridgePlugin.delete();

            File applicationFile = new File(folder, con.getVersion().getJar());
            applicationFile.delete();

        } else {
            //dynamic -> delete everything
            if (folder.exists()) {
                try {
                    FileUtils.deleteDirectory(folder);
                } catch (IOException e) {
                }
            }
        }

        if (screenManager.isScreenActive(screen.getName())) {
            screen.leave();
        }
        screenManager.unregisterScreen(service.getName());

        if (NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class) != null && NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).isHeadNode()) {
            DriverUpdatePacket.publishUpdate(CloudDriver.getInstance().getNetworkExecutor());
        }
    }

    @Override
    public Task<ICloudServer> startService(@NotNull ICloudServer service) {
        return worker.processService(service);
    }

    @Override
    public Task<ICloudServer> thisService() {
        return Task.empty();
    }

    @Override
    public ICloudServer thisServiceOrNull() {
        return null;
    }

    @Override
    public void sendPacketToService(@NotNull ICloudServer service, @NotNull IPacket packet) {
        NodeDriver.getInstance().getNetworkExecutor().getAllCachedConnectedClients().stream().filter(it -> it.getName().equals(service.getName())).findAny().ifPresent(it -> it.sendPacket(packet));
    }


    @Override
    public void shutdownService(ICloudServer service) {
        INode node = service.getTask().findAnyNode();
        node.stopServer(service);
    }

    @Override
    public void updateService(@NotNull ICloudServer service) {
        CloudDriver.getInstance().getLogger().debug("Updated Server {}", service.getName());
        this.updateServerInternally(service);

        //calling updateTask event on every other side
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventOnlyPacketBased(new ServiceUpdateEvent(service));
        if (NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class) != null && NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).isHeadNode()) {
            DriverUpdatePacket.publishUpdate(CloudDriver.getInstance().getNetworkExecutor());
        }
    }

    @EventListener
    public void handleStop(ServiceUnregisterEvent event) {
        ScreenManager sm = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ScreenManager.class);
        if (sm.isScreenActive(event.getService())) {
            sm.leaveCurrentScreen();
        }

    }

    @EventListener
    public void handleUpdate(ServiceUpdateEvent event) {
        ICloudServer server = event.getService();
        this.updateService(server);
    }

}
