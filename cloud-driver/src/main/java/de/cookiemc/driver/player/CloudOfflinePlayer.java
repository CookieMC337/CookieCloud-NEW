package de.cookiemc.driver.player;

import de.cookiemc.document.Document;
import de.cookiemc.driver.common.IdentityObject;
import de.cookiemc.driver.exception.PlayerNotOnlineException;
import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface CloudOfflinePlayer extends IBufferObject, IdentityObject {

    /**
     * The cached name of this offline player entry
     * (Note that names might have changed between log-ins)
     */
    @NotNull
    String getName();

    /**
     * Checks if this player is currently online
     */
    boolean isOnline();

    /**
     * Tries to get this player as online player
     */
    ICloudPlayer asOnlinePlayer() throws PlayerNotOnlineException;

    /**
     * Overrides the name of this cached player entry
     * because the name might have changed over time
     * to keep correct offline player entries
     *
     * @param name the name of this player entry
     */
    void setName(@Nonnull String name);

    /**
     * The cached uuid of this offline player entry
     */
    @NotNull
    UUID getUniqueId();

    /**
     * the temporary properties of this player
     */
    TemporaryProperties getTemporaryProperties();

    /**
     * The permanent properties of this player
     * That are stored as {@link Document}
     *
     * @see Document
     */
    @NotNull
    Document getProperties();

    /**
     * Overrides the properties to keep track of correct data
     *
     * @param properties the properties to set
     */
    void setProperties(@Nonnull Document properties);

    /**
     * The time as long (date in millis) when this player has
     * logged in for the first time on this network
     */
    long getFirstLogin();

    /**
     * Sets the first login of this player when joining
     * (Only modify if you know what you're doing)
     *
     * @param time the time to set
     */
    void setFirstLogin(long time);

    /**
     * The time as long (date in millis) when the player
     * lastly joined the network
     */
    long getLastLogin();

    /**
     * Sets the last login of this player when joining
     * (Only modify if you know what you're doing)
     *
     * @param time the time to set
     */
    void setLastLogin(long time);

    /**
     * Saves this player in database
     */
    void saveOfflinePlayer();

}
