package de.cookiemc.node.handler.packet.normal;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.networking.protocol.packets.defaults.StorageUpdatePacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.storage.INetworkDocumentStorage;

public class NodeStoragePacketHandler implements PacketHandler<StorageUpdatePacket> {

    @Override
    public void handle(PacketChannel wrapper, StorageUpdatePacket packet) {
        StorageUpdatePacket.StoragePayLoad payLoad = packet.getPayLoad();
        if (payLoad == StorageUpdatePacket.StoragePayLoad.FETCH) {
            wrapper.prepareResponse().data(CloudDriver.getInstance().getProviderRegistry().getUnchecked(INetworkDocumentStorage.class).getRawData()).execute(packet);
        } else {
            CloudDriver.getInstance().getProviderRegistry().getUnchecked(INetworkDocumentStorage.class).setRawData(packet.getStorage());
        }
    }
}
