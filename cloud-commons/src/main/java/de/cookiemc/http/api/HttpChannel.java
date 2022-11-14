package de.cookiemc.http.api;



import de.cookiemc.http.HttpAddress;

import javax.annotation.Nonnull;


public interface HttpChannel {

	@Nonnull
    HttpAddress getServerAddress();

	@Nonnull
	HttpAddress getClientAddress();

	void close();

}
