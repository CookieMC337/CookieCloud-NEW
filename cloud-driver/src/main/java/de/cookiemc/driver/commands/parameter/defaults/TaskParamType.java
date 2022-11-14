package de.cookiemc.driver.commands.parameter.defaults;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.parameter.CommandParameterType;
import de.cookiemc.driver.services.task.ICloudServiceTaskManager;
import de.cookiemc.driver.services.task.IServiceTask;

public class TaskParamType extends CommandParameterType<IServiceTask> {

    @Override
    public String label() {
        return "task";
    }

    @Override
    public IServiceTask resolve(String s) {
        return CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudServiceTaskManager.class).getTaskOrNull(s);
    }

    @Override
    public Class<IServiceTask> typeClass() {
        return IServiceTask.class;
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
