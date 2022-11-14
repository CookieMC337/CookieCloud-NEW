package de.cookiemc.driver.networking.protocol.packets;

import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;

public interface PacketHandler<T extends IPacket> {

    void handle(PacketChannel wrapper, T packet);
}
