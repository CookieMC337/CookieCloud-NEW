package de.cookiemc.modules.sign.cloud;

import de.cookiemc.common.logging.Logger;
import de.cookiemc.document.DocumentFactory;
import de.cookiemc.driver.message.ChannelMessage;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.modules.sign.api.CloudSignAPI;
import de.cookiemc.modules.sign.api.ICloudSignManager;
import de.cookiemc.modules.sign.api.protocol.SignProtocolType;
import de.cookiemc.modules.sign.cloud.manager.ModuleCloudSignManager;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class ModuleCloudSignAPI extends CloudSignAPI {

    private final ICloudSignManager signManager;

    public ModuleCloudSignAPI(ModuleBootstrap module) {
        super();

        this.signManager = new ModuleCloudSignManager(module.getController().getDataFolder());
        this.signManager.loadCloudSignsAsync().onTaskSucess(cloudSigns -> {
            Logger.constantInstance().debug("Signs-Module loaded {} Cloudsigns!", cloudSigns.size());
        });
    }

    @Override
    public void publishConfiguration() {
        this.performProtocolAction(
                SignProtocolType.SYNC_CONFIG,
                buf -> buf.writeDocument(
                        DocumentFactory.newJsonDocument(
                                this.getSignConfiguration()
                        )
                )
        );
    }

    @Override
    public void performProtocolAction(SignProtocolType type, Consumer<PacketBuffer> buffer) {

        ChannelMessage message = ChannelMessage
                .builder()
                .channel(CloudSignAPI.CHANNEL_NAME)
                .buffer(buf -> {
                    buf.writeEnum(type);
                    buf.append(buffer);
                }).build();
        message.send();
    }
}

