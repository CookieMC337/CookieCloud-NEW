package de.cookiemc.http.api;

import javax.annotation.Nonnull;


public interface HttpAuthUser {

	boolean hasPermission(@Nonnull String permission);

}
