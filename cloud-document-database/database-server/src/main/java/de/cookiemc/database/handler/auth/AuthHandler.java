package de.cookiemc.database.handler.auth;

import de.cookiemc.database.DocumentDatabase;
import de.cookiemc.database.handler.auth.user.AllowedAuthUser;
import de.cookiemc.http.api.HttpAuthHandler;
import de.cookiemc.http.api.HttpAuthUser;
import de.cookiemc.http.api.HttpContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class AuthHandler implements HttpAuthHandler {

    @Nullable
    @Override
    public HttpAuthUser getAuthUser(@Nonnull HttpContext context, @NotNull String token) {
        if (token.equalsIgnoreCase(DocumentDatabase.getInstance().getConfig().getToken())) {
            return new AllowedAuthUser();
        }

        return null;
    }
}
