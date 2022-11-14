package de.cookiemc.driver.permission;

import java.util.UUID;

public interface PermissionChecker {


    boolean hasPermission(UUID playerUniqueId, String permission);
}
