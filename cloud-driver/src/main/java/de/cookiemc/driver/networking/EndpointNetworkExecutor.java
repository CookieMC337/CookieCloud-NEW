package de.cookiemc.driver.networking;

import de.cookiemc.driver.networking.cluster.ClusterClientExecutor;
import de.cookiemc.driver.networking.protocol.packets.IPacket;

import java.util.Optional;

public interface EndpointNetworkExecutor extends IHandlerNetworkExecutor {

    void sendPacket(IPacket packet, NetworkComponent component);

    int getProxyStartPort();

    int getSpigotStartPort();
    Optional<ClusterClientExecutor> getClient(String name);
}
