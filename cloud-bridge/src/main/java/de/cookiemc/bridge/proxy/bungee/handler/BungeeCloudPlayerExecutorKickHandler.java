package de.cookiemc.bridge.proxy.bungee.handler;

import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.player.packet.CloudPlayerKickPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCloudPlayerExecutorKickHandler implements PacketHandler<CloudPlayerKickPacket> {

    @Override
    public void handle(PacketChannel wrapper, CloudPlayerKickPacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getUuid());
        if (player == null) {
            return;
        }
        player.disconnect(new TextComponent(packet.getReason()));
    }
}
