package de.cookiemc.driver.networking.protocol.packets;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;

public interface IPacket extends IBufferObject {


    PacketTransferInfo transferInfo();

    PacketBuffer buffer();


    Task<Void> publishAsync();

    void setDestinationChannel(String destinationChannel);

    String getDestinationChannel();

    void publish();

    void publishTo(String... receivers);

    Task<Void> publishToAsync(String... receivers);

    Task<BufferedResponse> awaitResponse();

    Task<BufferedResponse> awaitResponse(String receiver);
}
