package de.cookiemc.http.api;

import de.cookiemc.common.collection.pair.Pair;
import de.cookiemc.http.HttpAddress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;


public interface HttpServer {

	HttpServer addListener(@Nonnull HttpAddress address);

	void shutdown();

	@Nonnull
	Collection<WebSocketChannel> getWebSocketChannels();

	void sendWebSocketFrame(@Nonnull WebSocketFrameType type, @Nonnull byte[] data);

	void sendWebSocketFrame(@Nonnull WebSocketFrameType type, @Nonnull String text);

	@Nonnull
    HttpAuthRegistry getAuthRegistry();

	@Nonnull
    HttpHandlerRegistry getHandlerRegistry();

	void applyUserAuth(@Nonnull HttpContext context, @Nonnull Pair<HttpAuthHandler, HttpAuthUser> values, @Nullable String header);

}
