package de.cookiemc.driver.networking.protocol.packets;

import de.cookiemc.document.Document;
import de.cookiemc.driver.networking.NetworkComponent;

import java.util.UUID;

public interface PacketTransferInfo {

    UUID getInternalQueryId();

    void setInternalQueryId(UUID queryId);

    NetworkComponent getSender();

    Document getDocument();

}
