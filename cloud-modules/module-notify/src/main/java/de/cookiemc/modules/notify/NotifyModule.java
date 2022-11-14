package de.cookiemc.modules.notify;

import de.cookiemc.context.annotations.Constructor;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.ICommandManager;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.module.ModuleController;
import de.cookiemc.driver.module.controller.base.ModuleState;
import de.cookiemc.driver.module.controller.task.ModuleTask;
import de.cookiemc.modules.notify.command.NotifyCommand;
import de.cookiemc.modules.notify.config.NotifyConfiguration;
import de.cookiemc.modules.notify.listener.ModuleListener;
import lombok.Getter;

public class NotifyModule {

    /**
     * The static module instance
     */
    @Getter
    private static NotifyModule instance;

    /**
     * The configuration of this module
     */
    @Getter
    private NotifyConfiguration configuration;

    @Getter
    private final ModuleController controller;

    @Constructor
    public NotifyModule(ModuleController moduleController) {
        instance = this;

        this.controller = moduleController;
    }

    @ModuleTask(id = 1, state = ModuleState.LOADED)
    public void loadConfig() {

        if (controller.getConfig().isEmpty()) {
            controller.getConfig().set(configuration = new NotifyConfiguration());
            controller.getConfig().save();
        } else {
            configuration = controller.getConfig().toInstance(NotifyConfiguration.class);
        }
    }

    @ModuleTask(id = 2, state = ModuleState.ENABLED)
    public void enable() {
        //registering command and listener
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).registerListener(new ModuleListener());
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICommandManager.class).registerCommands(new NotifyCommand());
    }

}
