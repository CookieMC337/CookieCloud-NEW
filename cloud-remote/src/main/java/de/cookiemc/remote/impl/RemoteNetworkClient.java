package de.cookiemc.remote.impl;

import de.cookiemc.document.Document;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.networking.cluster.client.ClusterParticipant;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import io.netty.channel.ChannelHandlerContext;
import de.cookiemc.driver.networking.protocol.packets.ConnectionType;

public class RemoteNetworkClient extends ClusterParticipant {

    public RemoteNetworkClient(String authKey, String clientName, String hostname, int port, Document customData, Runnable... connectionFailed) {
        super(authKey, clientName, ConnectionType.SERVICE, customData);

        this.bootAsync().openConnection(hostname, port).registerListener(channelTask -> {
            if (channelTask.isPresent()) {
                CloudDriver.getInstance().getLogger().info("This service has connected to the Cluster!");
            } else {
                CloudDriver.getInstance().getLogger().info("This service couldn't connect to the Cluster!");
                channelTask.error().printStackTrace();
                for (Runnable runnable : connectionFailed) runnable.run();
            }
        });
    }

    @Override
    public void onAuthenticationChanged(PacketChannel wrapper) {
        CloudDriver.getInstance().getLogger().info("This service was authenticated by the cluster");
    }

    @Override
    public void onActivated(ChannelHandlerContext channelHandlerContext) {
        CloudDriver.getInstance().getLogger().info("This service successfully connected to the cluster.");
    }

    @Override
    public void onClose(ChannelHandlerContext channelHandlerContext) {
        CloudDriver.getInstance().getLogger().info("This service disconnected from the cluster");
    }

}
