package de.cookiemc.bridge.proxy.bungee.handler;


import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.player.packet.CloudPlayerTabListPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCloudPlayerExecutorTabHandler implements PacketHandler<CloudPlayerTabListPacket> {

    @Override
    public void handle(PacketChannel wrapper, CloudPlayerTabListPacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getUuid());
        if (player == null) {
            return;
        }

        String header = packet.getHeader();
        String footer = packet.getFooter();

        player.setTabHeader(
                new TextComponent(header),
                new TextComponent(footer)
        );
    }
}
