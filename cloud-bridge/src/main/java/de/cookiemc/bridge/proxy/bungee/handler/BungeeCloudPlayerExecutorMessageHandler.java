package de.cookiemc.bridge.proxy.bungee.handler;


import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.player.packet.CloudPlayerPlainMessagePacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCloudPlayerExecutorMessageHandler implements PacketHandler<CloudPlayerPlainMessagePacket> {

    @Override
    public void handle(PacketChannel wrapper, CloudPlayerPlainMessagePacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getUuid());
        if (player == null) {
            return;
        }
        player.sendMessage(packet.getMessage());
    }
}