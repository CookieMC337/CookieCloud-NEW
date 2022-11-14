package de.cookiemc.modules.sign.cloud;

import de.cookiemc.context.annotations.Constructor;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.ICommandManager;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.message.IChannelMessenger;
import de.cookiemc.driver.module.ModuleController;
import de.cookiemc.driver.module.controller.base.ModuleState;
import de.cookiemc.driver.module.controller.task.ModuleTask;
import de.cookiemc.modules.sign.api.CloudSignAPI;
import de.cookiemc.modules.sign.api.config.SignConfiguration;
import de.cookiemc.modules.sign.cloud.command.ModuleCloudSignCommand;
import de.cookiemc.modules.sign.cloud.handler.ModuleMessageHandler;
import de.cookiemc.modules.sign.cloud.listener.ModuleServiceReadyListener;
import lombok.Getter;

public class ModuleBootstrap {

    @Getter
    private final ModuleController controller;

    @Constructor
    public ModuleBootstrap(ModuleController controller) {
        this.controller = controller;
    }

    @ModuleTask(id = 1, state = ModuleState.LOADED)
    public void load() {
        new ModuleCloudSignAPI(this);
    }

    @ModuleTask(id = 2, state = ModuleState.LOADED)
    public void loadConfig() {
        SignConfiguration configuration;
        if (controller.getConfig().isEmpty()) {
            controller.getConfig().set(configuration = new SignConfiguration());
            controller.getConfig().save();
        } else {
            configuration = controller.getConfig().toInstance(SignConfiguration.class);
        }

        //setting value in api
        CloudSignAPI.getInstance().setSignConfiguration(configuration);
    }

    @ModuleTask(id = 3, state = ModuleState.ENABLED)
    public void enable() {
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IChannelMessenger.class).registerChannel(CloudSignAPI.CHANNEL_NAME, new ModuleMessageHandler());
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).registerListener(new ModuleServiceReadyListener());
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICommandManager.class).registerCommands(new ModuleCloudSignCommand());


        CloudSignAPI.getInstance().publishConfiguration();
        CloudSignAPI.getInstance().getSignManager().update();
    }


    @ModuleTask(id = 4, state = ModuleState.DISABLED)
    public void disable() {
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICommandManager.class).unregister(ModuleCloudSignCommand.class);
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IChannelMessenger.class).unregisterChannel(CloudSignAPI.CHANNEL_NAME);
    }

}
