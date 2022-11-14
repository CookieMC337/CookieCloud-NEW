package de.cookiemc.driver.networking;

import de.cookiemc.driver.networking.protocol.packets.IPacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

// TODO: 21.08.2022 documentation
public interface IHandlerNetworkExecutor extends INetworkExecutor {

    <T extends IPacket> void handlePacket(@Nullable PacketChannel wrapper, @Nonnull T packet);

    <T extends IPacket> void registerChannelHandler(@Nonnull String channelName, @Nonnull PacketHandler<T> packetHandler);

    <T extends IPacket> void unRegisterChannelHandler(@Nonnull String channelName, @Nonnull PacketHandler<T> packetHandler);

    void unregisterChannelHandlers(@Nonnull String channelName);

    <T extends IPacket> void registerPacketHandler(@Nonnull PacketHandler<T> packetHandler);

    <T extends IPacket> void registerSelfDestructivePacketHandler(@Nonnull PacketHandler<T> packetHandler);

    PacketChannel getPacketChannel();

    @Nonnull
    List<PacketHandler<?>> getRegisteredPacketHandlers();
}
