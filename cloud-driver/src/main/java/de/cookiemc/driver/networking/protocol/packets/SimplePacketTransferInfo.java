package de.cookiemc.driver.networking.protocol.packets;

import de.cookiemc.document.Document;
import de.cookiemc.driver.networking.NetworkComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class SimplePacketTransferInfo implements PacketTransferInfo {

    private UUID internalQueryId;
    private NetworkComponent sender;
    private Document document;

}
