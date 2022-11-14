package de.cookiemc.modules.hubcommand;

import de.cookiemc.context.annotations.Constructor;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.ICommandManager;
import de.cookiemc.driver.module.ModuleController;
import de.cookiemc.driver.module.controller.base.ModuleState;
import de.cookiemc.driver.module.controller.task.ModuleTask;
import de.cookiemc.modules.hubcommand.command.HubCommand;

public class HubCommandModule {

    private final ModuleController controller;

    @Constructor
    public HubCommandModule(ModuleController controller) {
        this.controller = controller;
    }

    @ModuleTask(id = 1, state = ModuleState.ENABLED)
    public void enable() {
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICommandManager.class).registerCommands(new HubCommand());
    }

    @ModuleTask(id = 3, state = ModuleState.DISABLED)
    public void disable() {
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICommandManager.class).unregister(HubCommand.class);
    }
}
