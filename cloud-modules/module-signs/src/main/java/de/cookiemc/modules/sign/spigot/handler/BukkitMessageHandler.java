package de.cookiemc.modules.sign.spigot.handler;

import de.cookiemc.driver.message.ChannelMessage;
import de.cookiemc.driver.message.ChannelMessageListener;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.modules.sign.api.def.UniversalCloudSign;
import de.cookiemc.modules.sign.api.CloudSignAPI;
import de.cookiemc.modules.sign.api.ICloudSignManager;
import de.cookiemc.modules.sign.api.config.SignConfiguration;
import de.cookiemc.modules.sign.api.protocol.SignProtocolType;

public class BukkitMessageHandler implements ChannelMessageListener {

    @Override
    public void handleIncoming(ChannelMessage message) {

        ICloudSignManager signManager = CloudSignAPI.getInstance().getSignManager();
        PacketBuffer buffer = message.buffer();

        switch (buffer.readEnum(SignProtocolType.class)) {
            case SYNC_CACHE:
                signManager.setAllCachedCloudSigns(buffer.readWrapperObjectCollection(UniversalCloudSign.class));
                break;
            case SYNC_CONFIG:
                CloudSignAPI.getInstance().setSignConfiguration(buffer.readDocument().toInstance(SignConfiguration.class));
                break;
        }
    }
}
