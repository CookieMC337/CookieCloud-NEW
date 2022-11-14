package de.cookiemc.discordbot.api.internal;

import de.cookiemc.document.Document;
import de.cookiemc.discordbot.api.wrapped.DiscordListener;
import de.cookiemc.discordbot.api.wrapped.DiscordServer;
import net.dv8tion.jda.api.events.GenericEvent;

import java.util.Collection;

public interface IBotService {

    void addListeners(Collection<DiscordListener<?>> listeners);

    String getIdentifier();

    Document handleSetup(DiscordServer server);

    void handleConfigLoad(Document document);
}
