package de.cookiemc.modules.perms;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.permission.*;
import de.cookiemc.driver.permission.*;
import de.cookiemc.modules.perms.global.impl.DefaultPermission;
import de.cookiemc.modules.perms.global.impl.DefaultPermissionGroup;
import de.cookiemc.modules.perms.global.impl.DefaultPermissionPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class DefaultPermissionManager implements PermissionManager {

    public DefaultPermissionManager() {
        CloudDriver.getInstance().getProviderRegistry().setProvider(PermissionChecker.class, this);
    }

    @Override
    public boolean hasPermission(UUID playerUniqueId, String permission) {
        PermissionPlayer p = getPlayerByUniqueIdOrNull(playerUniqueId);
        if (p == null) {
            return false;
        }
        return p.hasPermission("*") || p.hasPermission(permission);
    }

    @NotNull
    @Override
    public Permission createPermission(@NotNull String permission, @NotNull long expirationDate) {
        return new DefaultPermission(permission, expirationDate);
    }

    @Override
    public PermissionPlayer createPlayer(String name, UUID uniqueId) {
        return new DefaultPermissionPlayer(name, uniqueId);
    }

    @NotNull
    @Override
    public PermissionGroup createPermissionGroup(@NotNull String name) {
        return new DefaultPermissionGroup(name, "", "", "", "", "", 1, false, new ArrayList<>(), new HashMap<>());
    }
}
