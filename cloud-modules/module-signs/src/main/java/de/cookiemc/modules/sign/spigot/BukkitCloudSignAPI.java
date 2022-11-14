package de.cookiemc.modules.sign.spigot;

import de.cookiemc.driver.message.ChannelMessage;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.modules.sign.api.CloudSignAPI;
import de.cookiemc.modules.sign.api.ICloudSignManager;
import de.cookiemc.modules.sign.api.protocol.SignProtocolType;
import de.cookiemc.modules.sign.spigot.manager.BukkitCloudSignManager;
import de.cookiemc.modules.sign.spigot.manager.BukkitCloudSignUpdater;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class BukkitCloudSignAPI extends CloudSignAPI {

    /**
     * The sign manager instance
     */
    private final ICloudSignManager signManager;

    /**
     * The sign updater instance
     */
    private final BukkitCloudSignUpdater signUpdater;

    public BukkitCloudSignAPI() {
        super();

        this.signManager = new BukkitCloudSignManager();
        this.signUpdater = new BukkitCloudSignUpdater();

        new Thread(this.signUpdater, "signThread").start();
    }

    @Override
    public void publishConfiguration() {
    }

    @Override
    public void performProtocolAction(SignProtocolType type, Consumer<PacketBuffer> buffer) {

        ChannelMessage message = ChannelMessage
                .builder()
                .channel(CloudSignAPI.CHANNEL_NAME)
               // .receivers(NetworkComponent.of(CloudDriver.retrieveFromStorage().getServiceManager().thisServiceOrNull().getRunningNodeName(), ConnectionType.NODE))
                .buffer(buf -> {
                    buf.writeEnum(type);
                    buf.append(buffer);
                }).build();
        message.send();

    }
}
