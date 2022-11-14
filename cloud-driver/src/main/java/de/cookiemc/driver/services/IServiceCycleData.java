package de.cookiemc.driver.services;

import de.cookiemc.document.Document;
import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a Cycled-Data-Entry that cycles through the Cluster for given {@link ICloudServer}s
 * And can contain custom data and the latency of this to detect service timeout in {@link ICloudServer#isTimedOut()}
 *
 * @author CookieMC337
 * @since SNAPSHOT-1.3
 */
public interface IServiceCycleData extends IBufferObject {

    /**
     * The custom data of this cycle
     *
     * @see Document
     */
    @NotNull
    Document getData();

    /**
     * The latency of this cycle
     */
    int getLatency();

    /**
     * The timestamp this cycle was created
     */
    long getTimestamp();
}
