package de.cookiemc.driver.services.template.def;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.services.template.ITemplateManager;
import de.cookiemc.driver.services.template.ITemplate;
import de.cookiemc.driver.services.template.ITemplateStorage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CloudTemplate implements ITemplate {

    private String name;
    private String prefix;
    private String storageName;
    private boolean copyToStatic;

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {
        switch (state) {
            case WRITE:
                buf.writeString(prefix);
                buf.writeString(name);
                buf.writeString(storageName);
                buf.writeBoolean(copyToStatic);
                break;
            case READ:
                prefix = buf.readString();
                name = buf.readString();
                storageName = buf.readString();
                copyToStatic = buf.readBoolean();
                break;
        }
    }


    @Override
    public @NotNull String buildTemplatePath() {
        return this.name + "/" + this.prefix;
    }

    @Override
    public ITemplateStorage getStorage() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ITemplateManager.class).getStorage(this.getStorageName());
    }

    @Override
    public boolean shouldCopyToStatic() {
        return copyToStatic;
    }
}
