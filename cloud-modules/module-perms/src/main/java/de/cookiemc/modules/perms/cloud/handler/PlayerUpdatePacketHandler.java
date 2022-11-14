package de.cookiemc.modules.perms.cloud.handler;


import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.permission.PermissionPlayer;
import de.cookiemc.modules.perms.global.packets.PermsPlayerUpdatePacket;

public class PlayerUpdatePacketHandler implements PacketHandler<PermsPlayerUpdatePacket> {

    @Override
    public void handle(PacketChannel wrapper, PermsPlayerUpdatePacket packet) {
        PermissionPlayer player = packet.getPlayer();
        player.update();
    }
}
