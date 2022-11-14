package de.cookiemc.modules.perms.global.impl;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.parameter.CommandParameterType;
import de.cookiemc.driver.permission.PermissionManager;
import de.cookiemc.driver.permission.PermissionPlayer;

public class ParameterPermissionPlayer extends CommandParameterType<PermissionPlayer> {
    @Override
    public String label() {
        return "permissionPlayer";
    }

    @Override
    public PermissionPlayer resolve(String s) {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(PermissionManager.class).getPlayerByNameOrNull(s);
    }

    @Override
    public Class<PermissionPlayer> typeClass() {
        return PermissionPlayer.class;
    }

    @Override
    public boolean checkCustom(String arg, String s) {
        return false;
    }

    @Override
    public String handleCustomException(String s) {
        return null;
    }
}
