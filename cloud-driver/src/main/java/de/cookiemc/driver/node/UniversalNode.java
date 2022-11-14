package de.cookiemc.driver.node;

import de.cookiemc.common.function.ExceptionallyConsumer;
import de.cookiemc.common.misc.StringUtils;
import de.cookiemc.common.task.Task;
import de.cookiemc.driver.networking.protocol.packets.defaults.DriverLoggingPacket;
import de.cookiemc.driver.node.packet.NodeRequestServerStartPacket;
import de.cookiemc.driver.node.packet.NodeRequestServerStopPacket;
import de.cookiemc.driver.node.packet.NodeRequestShutdownPacket;
import de.cookiemc.driver.networking.protocol.packets.BufferedResponse;
import de.cookiemc.driver.networking.protocol.packets.IPacket;
import de.cookiemc.driver.networking.protocol.packets.NetworkResponseState;
import de.cookiemc.driver.node.base.AbstractNode;
import de.cookiemc.driver.node.config.INodeConfig;
import de.cookiemc.driver.node.data.INodeCycleData;
import de.cookiemc.driver.services.ICloudServer;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@Setter
public class UniversalNode extends AbstractNode {

    public UniversalNode(INodeConfig config, INodeCycleData lastCycleData) {
        super(config, lastCycleData);
    }

    @Override
    public void sendPacket(IPacket packet) {
        packet.publishTo(this.getName());
    }

    @Override
    public Task<Void> sendPacketAsync(IPacket packet) {
        return packet.publishToAsync(this.getName());
    }

    @Override
    public void shutdown() {
        this.sendPacket(new NodeRequestShutdownPacket(this.getName()));
    }

    @Override
    public void log(String message, Object... args) {
        this.sendPacket(new DriverLoggingPacket(this, StringUtils.formatMessage(message, args)));
    }

    @Override
    public void stopServer(ICloudServer server) {
        this.sendPacket(new NodeRequestServerStopPacket(server.getName(), false));
    }

    @Override
    public @NotNull Task<NetworkResponseState> stopServerAsync(@NotNull ICloudServer server) {
        return new NodeRequestServerStartPacket(server, true)
                .awaitResponse(this.getName())
                .registerListener((ExceptionallyConsumer<Task<BufferedResponse>>) task -> {
                    if (!task.isPresent()) {
                        task.setFailure(task.error());
                    }
                }).map(BufferedResponse::state);
    }

    @Override
    public void startServer(@NotNull ICloudServer server) {
        this.sendPacket(new NodeRequestServerStartPacket(server, false));
    }

    @Override
    public @NotNull Task<NetworkResponseState> startServerAsync(ICloudServer server) {
        return new NodeRequestServerStopPacket(server.getName(), true)
                .awaitResponse(this.getName())
                .registerListener((ExceptionallyConsumer<Task<BufferedResponse>>) task -> {
                    if (!task.isPresent()) {
                        task.setFailure(task.error());
                    }
                }).map(BufferedResponse::state);
    }

}
