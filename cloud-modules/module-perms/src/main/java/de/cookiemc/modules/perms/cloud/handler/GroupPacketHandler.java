package de.cookiemc.modules.perms.cloud.handler;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.permission.PermissionManager;
import de.cookiemc.modules.perms.global.packets.PermsGroupPacket;

public class GroupPacketHandler implements PacketHandler<PermsGroupPacket> {

    @Override
    public void handle(PacketChannel wrapper, PermsGroupPacket packet) {
        PermissionManager permissionManager = CloudDriver.getInstance().getProviderRegistry().getUnchecked(PermissionManager.class);
        switch (packet.getPayLoad()) {
            case CREATE:
                permissionManager.addPermissionGroup(packet.getGroup());
                break;
            case REMOVE:
                String name = packet.getName();
                permissionManager.deletePermissionGroup(name);
                break;
            case UPDATE:
                permissionManager.updatePermissionGroup(packet.getGroup());
                break;
        }
    }
}
