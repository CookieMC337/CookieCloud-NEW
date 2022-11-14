package de.cookiemc.node.handler.packet.normal;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.ICommandManager;
import de.cookiemc.driver.player.ICloudPlayerManager;
import de.cookiemc.driver.player.packet.CloudPlayerExecuteCommandPacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.commands.context.defaults.PlayerCommandContext;

import java.util.UUID;

public class NodePlayerCommandHandler implements PacketHandler<CloudPlayerExecuteCommandPacket> {
    @Override
    public void handle(PacketChannel wrapper, CloudPlayerExecuteCommandPacket packet) {
        String commandLine = packet.getCommandLine();
        UUID uuid = packet.getUuid();
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getCloudPlayer(uuid).ifPresent(cloudPlayer -> {

            CloudDriver.getInstance().getLogger().debug("Player [name={}, uuid={}] executed CloudSided-Ingame-command: '{}'", cloudPlayer.getName(), cloudPlayer.getUniqueId(), commandLine);
            CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICommandManager.class).executeCommand(commandLine.split(" "), new PlayerCommandContext(cloudPlayer));
        });
    }
}
