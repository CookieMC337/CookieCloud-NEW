package de.cookiemc.discordbot.bot.service.suggestion;

import de.cookiemc.document.Document;
import de.cookiemc.discordbot.api.wrapped.DiscordListener;
import de.cookiemc.discordbot.api.wrapped.DiscordServer;
import lombok.Getter;
import de.cookiemc.discordbot.api.internal.IBotService;

import java.util.Collection;

@Getter
public class SuggestionService implements IBotService {

    private String categoryId, channelId, resultChannelId;

    @Override
    public void addListeners(Collection<DiscordListener<?>> listeners) {

    }

    @Override
    public String getIdentifier() {
        return "suggestion";
    }

    @Override
    public Document handleSetup(DiscordServer server) {

        /*
        Category category = server.createCategoryAsync("suggestion-c")
                .syncUninterruptedly()
                .get();
        System.out.println(2);
        ForumChannel channel = server.createForumChannelAsync(
                        "suggestions",
                        category,
                        new ForumTagData("Cloud"),
                        new ForumTagData("Module(s)"),
                        new ForumTagData("API"),
                        new ForumTagData("Other")
                )
                .syncUninterruptedly()
                .get();
        TextChannel resultChannel = server.createTextChannelAsync("results", category)
                .syncUninterruptedly()
                .get();


        channel.createForumPost("Your Title", MessageCreateData.fromEmbeds(
                new EmbedBuilder()
                        .setColor(Color.CYAN)
                        .setDescription("A short description of your suggestion")
                        .setImage(CookieCloudDiscordBot.getInstance().getDiscord().getBotUser().getEffectiveAvatarUrl())
                        .build()
        )).queue(post -> {
            post.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDC4D")).queue();
            post.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDC4E")).queue();
        });
        return Document.newJsonDocument(
                "categoryId", category.getId(),
                "channelId", channel.getId(),
                "resultChannelId", resultChannel.getId()
        );*/
        return Document.newJsonDocument();
    }

    @Override
    public void handleConfigLoad(Document document) {
        this.categoryId = document.getString("categoryId");
        this.channelId = document.getString("channelId");
        this.resultChannelId = document.getString("resultChannelId");
    }
}
