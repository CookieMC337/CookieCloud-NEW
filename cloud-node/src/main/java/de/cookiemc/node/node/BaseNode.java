package de.cookiemc.node.node;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.services.ICloudServiceManager;
import de.cookiemc.driver.services.IProcessCloudServer;
import de.cookiemc.driver.services.packet.ServiceForceShutdownPacket;
import de.cookiemc.driver.networking.protocol.packets.IPacket;
import de.cookiemc.driver.networking.protocol.packets.NetworkResponseState;
import de.cookiemc.driver.node.base.AbstractNode;
import de.cookiemc.driver.node.data.DefaultNodeCycleData;
import de.cookiemc.driver.node.data.INodeCycleData;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.node.NodeDriver;
import de.cookiemc.node.config.ConfigManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BaseNode extends AbstractNode {

    public BaseNode(ConfigManager configManager) {
        super(configManager.getConfig().getNodeConfig(), DefaultNodeCycleData.current());
    }

    @Override
    public void sendPacket(IPacket packet) {
        NodeDriver.getInstance().getNetworkExecutor().handlePacket(NodeDriver.getInstance().getNetworkExecutor().getPacketChannel(), packet);
    }


    @Override
    public Task<Void> sendPacketAsync(IPacket packet) {
        return Task.callAsync(() -> {
            sendPacket(packet);
            return null;
        });
    }

    @Override
    public void log(String message, Object... args) {
        NodeDriver.getInstance().getLogger().info(message, args);
    }

    @Override
    public void shutdown() {
        NodeDriver.getInstance().shutdown();
    }


    @Override
    public @NotNull List<ICloudServer> getRunningServers() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getAllCachedServices().stream().filter(s -> {
            s.getTask();
            return s.getTask().getPossibleNodes().contains(this.config.getNodeName());
        }).collect(Collectors.toList());
    }

    @Override
    public @NotNull Task<Collection<ICloudServer>> getRunningServersAsync() {
        return Task.callAsync(() -> CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getAllCachedServices().stream().filter(s -> {
            s.getTask();
            return s.getRunningNodeName().equalsIgnoreCase(this.config.getNodeName());
        }).collect(Collectors.toList()));
    }

    @Override
    public @NotNull DefaultNodeCycleData getLastCycleData() {
        return DefaultNodeCycleData.current();
    }

    @Override
    public void setLastCycleData(@NotNull INodeCycleData lastCycleData) {
    }

    @Override
    public void stopServer(ICloudServer server) {
        server.sendPacket(new ServiceForceShutdownPacket(server.getName()));
        Task.runTaskLater(() -> {
            Process process = ((IProcessCloudServer)server).getProcess();
            if (process == null) {
                return;
            }
            process.destroyForcibly();
        }, TimeUnit.MILLISECONDS, 200);
    }

    @Override
    public @NotNull Task<NetworkResponseState> stopServerAsync(@NotNull ICloudServer server) {
        return Task.callAsync(() -> {
            stopServer(server);
            return NetworkResponseState.OK;
        });
    }


    @Override
    public void startServer(@NotNull ICloudServer server) {
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).startService(server);
    }

    @Override
    public @NotNull Task<NetworkResponseState> startServerAsync(@NotNull ICloudServer server) {
        return Task.callAsync(() -> {
            startServer(server);
            return NetworkResponseState.OK;
        });
    }



}
