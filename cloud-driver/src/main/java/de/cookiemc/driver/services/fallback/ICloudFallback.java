package de.cookiemc.driver.services.fallback;

import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link ICloudFallback} describes different configuration-entries
 * to define where to send you when needing a fallback (e.g. kicked from a sub-server)
 *
 * @author CookieMC337
 * @since SNAPSHOT-1.2
 */
public interface ICloudFallback extends IBufferObject {

    /**
     * If this fallback is currently active to be chosen
     */
    boolean isEnabled();

    /**
     * Enables using this fallback
     *
     * @param enabled the state
     * @see #isEnabled()
     */
    void setEnabled(boolean enabled);

    /**
     * The permission you need to have to be able
     * to access this fallback
     */
    @NotNull
    String getPermission();

    /**
     * The permission for this fallback
     *
     * @param permission the required permission
     * @see #getPermission()
     */
    void setPermission(@NotNull String permission);

    /**
     * The priority this fallback has when sorting between
     * available fallbacks for all or a specific player.
     * Lower priority gets chosen more likely
     */
    int getPriority();

    /**
     * Sets the priority for this fallback
     *
     * @param priority the priority to set
     * @see #getPriority()
     */
    void setPriority(int priority);

}
