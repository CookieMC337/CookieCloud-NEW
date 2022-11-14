package de.cookiemc.discordbot.api.wrapped;

import net.dv8tion.jda.api.events.GenericEvent;
import de.cookiemc.discordbot.bot.CookieCloudDiscordBot;

public interface DiscordListener<T extends GenericEvent> {

    default Discord discord() {
        return CookieCloudDiscordBot.getInstance().getDiscord();
    }

    void handleEvent(T event);
}
