package de.cookiemc.node.message;

import de.cookiemc.driver.message.ChannelMessage;
import de.cookiemc.driver.message.DefaultChannelMessenger;
import de.cookiemc.driver.networking.NetworkComponent;
import de.cookiemc.driver.networking.cluster.ClusterClientExecutor;
import de.cookiemc.driver.message.packet.ChannelMessageExecutePacket;
import de.cookiemc.driver.networking.protocol.packets.AbstractPacket;
import de.cookiemc.node.NodeDriver;
import de.cookiemc.node.node.NodeBasedClusterExecutor;

import java.util.Optional;

public class NodeChannelMessenger extends DefaultChannelMessenger {

    @Override
    public void sendChannelMessage(ChannelMessage message, NetworkComponent[] receiver) {
        NodeBasedClusterExecutor executor = NodeDriver.getInstance().getNetworkExecutor();
        AbstractPacket packet = new ChannelMessageExecutePacket(message);

        if (receiver.length > 0) {
            for (NetworkComponent messageReceiver : receiver) {
                Optional<ClusterClientExecutor> client = executor.getClient(messageReceiver.getName());
                client.ifPresent(clusterClientExecutor -> clusterClientExecutor.sendPacket(packet));
            }
        } else {
            executor.sendPacketToAll(packet);
        }
    }
}
