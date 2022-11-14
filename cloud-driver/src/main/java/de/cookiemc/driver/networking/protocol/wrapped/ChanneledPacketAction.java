package de.cookiemc.driver.networking.protocol.wrapped;

import de.cookiemc.common.task.Task;
import de.cookiemc.document.Document;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.IPacket;
import de.cookiemc.driver.networking.protocol.packets.NetworkResponseState;
import de.cookiemc.driver.networking.protocol.packets.ConnectionType;

import java.util.function.Consumer;

public interface ChanneledPacketAction<R> {

    ChanneledPacketAction<R> state(NetworkResponseState state);

    ChanneledPacketAction<R> error(Throwable state);

    ChanneledPacketAction<R> data(Document document);

    ChanneledPacketAction<R> buffer(Consumer<PacketBuffer> buf);

    ChanneledPacketAction<R> buffer(PacketBuffer buf);

    ChanneledPacketAction<R> receivers(String... receivers);

    ChanneledPacketAction<R> receivers(ConnectionType... types);

    Task<R> execute(IPacket packet);

}
