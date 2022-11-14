package de.cookiemc.database.handler.auth.user;

import de.cookiemc.http.api.HttpAuthUser;
import org.jetbrains.annotations.NotNull;

public class AllowedAuthUser implements HttpAuthUser {

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }
}
