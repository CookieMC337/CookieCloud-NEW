package de.cookiemc.discordbot.bot.service.command;

import de.cookiemc.document.Document;
import de.cookiemc.discordbot.api.internal.IBotService;
import de.cookiemc.discordbot.api.wrapped.DiscordListener;
import de.cookiemc.discordbot.api.wrapped.DiscordServer;
import de.cookiemc.discordbot.bot.CookieCloudDiscordBot;
import de.cookiemc.discordbot.bot.service.command.impl.MemeCommand;
import de.cookiemc.discordbot.bot.service.command.listener.CommandListener;

import java.util.Collection;

public class CommandService implements IBotService {

    public CommandService() {
        CookieCloudDiscordBot bot = CookieCloudDiscordBot.getInstance();

        //registering commands
        bot.getCommandManager().registerCommands(new MemeCommand());
    }

    @Override
    public void addListeners(Collection<DiscordListener<?>> listeners) {
        listeners.add(new CommandListener());
    }

    @Override
    public String getIdentifier() {
        return "commands";
    }

    @Override
    public Document handleSetup(DiscordServer server) {
        return null;
    }

    @Override
    public void handleConfigLoad(Document document) {
    }
}
