package de.cookiemc.driver.player.connection;

import de.cookiemc.http.ProtocolAddress;
import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;

import javax.annotation.Nonnull;

public interface PlayerConnection extends IBufferObject {

	@Nonnull
	String getProxyName();

	@Nonnull
    ProtocolAddress getAddress();

	@Nonnull
	ProtocolVersion getVersion();

	int getRawVersion();

	boolean isOnlineMode();

	boolean isLegacy();

}
