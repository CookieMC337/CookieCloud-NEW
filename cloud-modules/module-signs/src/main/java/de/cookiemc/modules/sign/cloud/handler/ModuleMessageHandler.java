package de.cookiemc.modules.sign.cloud.handler;

import de.cookiemc.driver.message.ChannelMessage;
import de.cookiemc.driver.message.ChannelMessageListener;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.modules.sign.api.def.UniversalCloudSign;
import de.cookiemc.modules.sign.api.CloudSignAPI;
import de.cookiemc.modules.sign.api.ICloudSign;
import de.cookiemc.modules.sign.api.ICloudSignManager;
import de.cookiemc.modules.sign.api.protocol.SignProtocolType;

public class ModuleMessageHandler implements ChannelMessageListener {

    @Override
    public void handleIncoming(ChannelMessage message) {
        ICloudSignManager signManager = CloudSignAPI.getInstance().getSignManager();
        PacketBuffer buffer = message.buffer();

        switch (buffer.readEnum(SignProtocolType.class)) {
            case ADD_SIGN:
                ICloudSign cloudSign = buffer.readObject(UniversalCloudSign.class);
                signManager.addCloudSign(cloudSign);
                break;
            case REQUEST_DATA:
                CloudSignAPI.getInstance().publishConfiguration();
                CloudSignAPI.getInstance().getSignManager().update();
            case REMOVE_SIGN:
                try {

                    ICloudSign sign = buffer.readObject(UniversalCloudSign.class);
                    ICloudSign safeSign = signManager.getCloudSignOrNull(sign.getUniqueId());
                    signManager.removeCloudSign(safeSign);
                } catch (Exception e) {
                    //ignore
                }
                break;
        }
    }
}
