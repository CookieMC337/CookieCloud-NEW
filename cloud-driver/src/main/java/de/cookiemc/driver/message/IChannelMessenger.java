package de.cookiemc.driver.message;

import de.cookiemc.driver.networking.NetworkComponent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface IChannelMessenger {


    /**
     * Registers listener for a given channel
     *
     * @param channel the channel
     * @param consumer the listener as consumer
     */
    void registerChannel(String channel, ChannelMessageListener consumer);

    /**
     * Unregisters a channel to listen for
     *
     * @param channel the channel
     */
    void unregisterChannel(String channel);

    /**
     * Sends a {@link ChannelMessage} to the receiver of it
     *
     * @param message the message
     */
    default void sendChannelMessage(ChannelMessage message) {
        this.sendChannelMessage(message, message.getReceivers());
    }

    /**
     * Sends a {@link ChannelMessage} to a given {@link NetworkComponent} receiver
     *
     * @param message the message
     * @param receivers the receivers
     */
    void sendChannelMessage(ChannelMessage message, NetworkComponent[] receivers);

    /**
     * Gets a list of all registered channels
     *
     * @return list of channels
     */
    default List<String> getChannel() {
        return new LinkedList<>(getCache().keySet());
    }

    /**
     * The whole cache for channel and listeners
     *
     * @return cache
     */
    Map<String, ChannelMessageListener> getCache();
}
