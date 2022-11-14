package de.cookiemc.modules.sign.spigot;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.ICommandManager;
import de.cookiemc.driver.message.IChannelMessenger;
import de.cookiemc.modules.sign.api.CloudSignAPI;
import de.cookiemc.modules.sign.spigot.command.BukkitSignCloudCommand;
import de.cookiemc.modules.sign.spigot.handler.BukkitMessageHandler;
import de.cookiemc.modules.sign.spigot.listener.PlayerSignListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class BukkitBootstrap extends JavaPlugin {

    @Getter
    private static BukkitBootstrap instance;

    @Override
    public void onEnable() {
        instance = this;
        new BukkitCloudSignAPI();

        Bukkit.getPluginManager().registerEvents(new PlayerSignListener(), this);

        CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICommandManager.class).registerCommands(new BukkitSignCloudCommand());
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IChannelMessenger.class).registerChannel(CloudSignAPI.CHANNEL_NAME, new BukkitMessageHandler());


        CloudSignAPI.getInstance().getSignManager().loadCloudSignsSync();
    }

    @Override
    public void onDisable() {
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICommandManager.class).unregister(BukkitSignCloudCommand.class);
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IChannelMessenger.class).unregisterChannel(CloudSignAPI.CHANNEL_NAME);
    }
}
