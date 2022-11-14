package de.cookiemc.modules.perms.cloud.handler;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.permission.PermissionManager;
import de.cookiemc.modules.perms.global.packets.PermsPlayerRequestPacket;

import java.util.UUID;

public class PlayerPacketHandler implements PacketHandler<PermsPlayerRequestPacket> {

    @Override
    public void handle(PacketChannel wrapper, PermsPlayerRequestPacket packet) {
        String name = packet.getName();
        UUID uniqueId = packet.getUniqueId();


        PermissionManager permissionManager = CloudDriver.getInstance().getProviderRegistry().getUnchecked(PermissionManager.class);

        if (name == null) {
            permissionManager.getPlayerAsyncByUniqueId(uniqueId).onTaskSucess(player -> {
               wrapper.prepareResponse().buffer(buf -> buf.writeObject(player)).execute(packet);
            });
        } else {
            permissionManager.getPlayerAsyncByName(name).onTaskSucess(player -> {
                wrapper.prepareResponse().buffer(buf -> buf.writeObject(player)).execute(packet);
            });
        }
    }
}
