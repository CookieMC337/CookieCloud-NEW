package de.cookiemc.driver.commands.parameter.defaults;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.parameter.CommandParameterType;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.ICloudServiceManager;

public class ServiceParamType extends CommandParameterType<ICloudServer> {

    @Override
    public String label() {
        return "server";
    }

    @Override
    public ICloudServer resolve(String s) {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceManager.class).getService(s);
    }

    @Override
    public Class<ICloudServer> typeClass() {
        return ICloudServer.class;
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
