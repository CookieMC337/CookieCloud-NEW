package de.cookiemc.node.handler.packet.normal;

import de.cookiemc.document.Document;
import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import de.cookiemc.driver.networking.protocol.codec.buf.defaults.BufferedDocument;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.packets.defaults.GenericQueryPacket;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.sync.ISyncedNetworkPromise;
import de.cookiemc.driver.sync.SyncedObjectType;
import de.cookiemc.node.NodeDriver;

public class NodeSyncPacketHandler implements PacketHandler<GenericQueryPacket<?>> {


    @Override
    public void handle(PacketChannel wrapper, GenericQueryPacket<?> packet) {
        String key = packet.getKey();
        if (key.equalsIgnoreCase("cloud_internal_sync")) {
            IBufferObject request = packet.getRequest();
            if (request instanceof Document || request instanceof BufferedDocument) {

                Document document = (request instanceof Document) ? (Document)request : ((BufferedDocument)request).getWrapped();

                int id = document.get("id").toInt();

                String parameter = document.get("parameter").toString();
                SyncedObjectType<?> type = SyncedObjectType.fromId(id);

                if (type == null) {
                    throw new NullPointerException("Couldn't find any SyncedObjectType with id '" + id + "'!");
                }

                ISyncedNetworkPromise<?> object = NodeDriver.getInstance().getSyncedNetworkObject(type, parameter);
                IBufferObject syncedObjectOrNull = object.getSyncedObjectOrNull();
                packet.respond(syncedObjectOrNull);
                //responding to request of query
            } else {
                throw new IllegalStateException("Received GenericQuery with key '" + key + "' but the provided data was not a Document but a " + request.getClass().getSimpleName() + "!");
            }
        }
    }
}
