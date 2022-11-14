package de.cookiemc.driver.networking.protocol.wrapped;

import de.cookiemc.driver.networking.IPacketExecutor;
import de.cookiemc.driver.networking.protocol.packets.BufferedResponse;
import de.cookiemc.driver.networking.protocol.packets.ConnectionState;
import de.cookiemc.driver.networking.protocol.packets.IPacket;
import de.cookiemc.driver.networking.INetworkExecutor;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Set;

public interface PacketChannel extends IPacketExecutor {

    /**
     * If a connection has been built up before
     */
    boolean hasEverConnected();

    /**
     * Default method to check if this wrapper is connected
     * by checking if the {@link ConnectionState} equals {@link ConnectionState#CONNECTED}
     */
    default boolean isConnected() {
        return state() == ConnectionState.CONNECTED;
    }

    /**
     * Flushes a {@link Packet} into this context
     *
     * @param packet the packet to send
     */
    void flushPacket(IPacket packet);

    /**
     * Closes this context
     */
    void close();

    /**
     * The wrapped netty channel context
     */
    ChannelHandlerContext context();

    /**
     * The wrapped context in an optional instance
     */
    Optional<ChannelHandlerContext> optional();

    /**
     * The state of this context
     */
    ConnectionState state();

    /**
     * The executor for this packet
     */
    INetworkExecutor executor();

    /**
     * The last modification time
     */
    long modificationTime();

    /**
     * The {@link InetSocketAddress} of this context
     */
    InetSocketAddress getClientAddress();

    PacketChannel overrideExecutor(INetworkExecutor executor);

    ChanneledPacketAction<Set<BufferedResponse>> prepareMultiQuery();

    ChanneledPacketAction<BufferedResponse> prepareSingleQuery();

    ChanneledPacketAction<Void> prepareResponse();

    ChanneledPacketAction<Void> prepareTransfer();

}
