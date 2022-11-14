package de.cookiemc.remote.impl;

import de.cookiemc.driver.message.ChannelMessage;
import de.cookiemc.driver.message.DefaultChannelMessenger;
import de.cookiemc.driver.networking.NetworkComponent;
import de.cookiemc.driver.message.packet.ChannelMessageExecutePacket;
import de.cookiemc.driver.networking.protocol.packets.ConnectionType;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.networking.protocol.wrapped.ChanneledPacketAction;

import java.util.Arrays;

public class RemoteChannelMessenger extends DefaultChannelMessenger {


    @Override
    public void sendChannelMessage(ChannelMessage message, NetworkComponent[] receivers) {
        PacketChannel wrapper = this.executor.getPacketChannel();

        ChanneledPacketAction<Void> transfer = wrapper.prepareTransfer();

        //declare receiver names and types
        transfer.receivers(Arrays.stream(receivers).map(NetworkComponent::getName).toArray(String[]::new));
        transfer.receivers(Arrays.stream(receivers).map(NetworkComponent::getType).toArray(ConnectionType[]::new));

        //send packet
        transfer.execute(new ChannelMessageExecutePacket(message));
    }
}
