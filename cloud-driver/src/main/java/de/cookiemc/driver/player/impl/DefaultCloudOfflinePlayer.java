package de.cookiemc.driver.player.impl;

import de.cookiemc.document.Document;
import de.cookiemc.document.DocumentFactory;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.exception.PlayerNotOnlineException;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import de.cookiemc.driver.player.CloudOfflinePlayer;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.player.ICloudPlayerManager;
import de.cookiemc.driver.player.TemporaryProperties;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.UUID;


@NoArgsConstructor
@Getter
@AllArgsConstructor
@Setter
@ToString
public class DefaultCloudOfflinePlayer implements CloudOfflinePlayer {

    protected UUID uniqueId;
    protected String name;
    protected long firstLogin;
    protected long lastLogin;
    protected Document properties;

    /**
     * The temporary properties
     */
    protected DefaultTemporaryProperties temporaryProperties = new DefaultTemporaryProperties();

    @Override
    public void applyBuffer(BufferState state, @NotNull PacketBuffer buffer) throws IOException {
        switch (state) {
            case WRITE:
                buffer.writeUniqueId(uniqueId);
                buffer.writeString(name);
                buffer.writeLong(firstLogin);
                buffer.writeLong(lastLogin);
                buffer.writeDocument(properties);
                buffer.writeDocument(DocumentFactory.newJsonDocument(temporaryProperties));
                break;

            case READ:
                uniqueId = buffer.readUniqueId();
                name = buffer.readString();
                firstLogin = buffer.readLong();
                lastLogin = buffer.readLong();
                properties = buffer.readDocument();
                Document tp = buffer.readDocument();
                this.temporaryProperties = tp.toInstance(DefaultTemporaryProperties.class);
                break;
        }
    }

    @Override
    public boolean isOnline() {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getCloudPlayerByUniqueIdOrNull(this.uniqueId) != null;
    }

    @Override
    public ICloudPlayer asOnlinePlayer() throws PlayerNotOnlineException {
        if (this.isOnline()) {
            return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getCloudPlayerByUniqueIdOrNull(this.uniqueId);
        }
        throw new PlayerNotOnlineException();
    }

    @Override
    public TemporaryProperties getTemporaryProperties() {

        for (String propertyName : temporaryProperties.getPropertyNames()) {
            if (temporaryProperties.hasPropertyExpired(propertyName)) {
                temporaryProperties.removeProperty(propertyName);
            }
        }
        this.saveOfflinePlayer();
        return this.temporaryProperties;
    }

    @Override
    public void saveOfflinePlayer() {
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).saveOfflinePlayerAsync(this);
    }

    @Nonnull
    @Override
    public Document getProperties() {
        return properties;
    }

    @Override
    public String getMainIdentity() {
        return uniqueId.toString();
    }
}
