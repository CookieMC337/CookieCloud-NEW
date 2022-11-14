package de.cookiemc.driver.commands.parameter.defaults;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.parameter.CommandParameterType;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.player.ICloudPlayerManager;

public class PlayerParamType extends CommandParameterType<ICloudPlayer> {

    @Override
    public String label() {
        return "player";
    }

    @Override
    public ICloudPlayer resolve(String s) {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudPlayerManager.class).getCloudPlayerByNameOrNull(s);
    }

    @Override
    public Class<ICloudPlayer> typeClass() {
        return ICloudPlayer.class;
    }

    @Override
    public boolean checkCustom(String arg, String s) {
        return true;
    }

    @Override
    public String handleCustomException(String s) {
        return null;
    }
}
