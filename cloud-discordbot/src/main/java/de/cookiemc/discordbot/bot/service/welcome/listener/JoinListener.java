package de.cookiemc.discordbot.bot.service.welcome.listener;

import de.cookiemc.discordbot.api.wrapped.DiscordListener;
import de.cookiemc.discordbot.api.wrapped.DiscordServer;
import de.cookiemc.discordbot.bot.service.welcome.WelcomeService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import de.cookiemc.discordbot.bot.CookieCloudDiscordBot;
import de.cookiemc.discordbot.bot.service.rules.RuleService;

import java.awt.*;

public class JoinListener implements DiscordListener<GuildMemberJoinEvent> {

    @Override
    public void handleEvent(GuildMemberJoinEvent event) {
        Member member = event.getMember();

        WelcomeService ser = CookieCloudDiscordBot.getInstance().getService("welcome");
        String welcomeChannelId = ser.getWelcomeChannelId();

        DiscordServer server = discord().getRunningServer(event.getGuild().getId());

        if (server == null) {
            return;
        }

        TextChannel textChannelById = server.wrapped().getTextChannelById(welcomeChannelId);

        if (textChannelById == null) {
            return;
        }

        RuleService ruleService = CookieCloudDiscordBot.getInstance().getService("rules");
        TextChannel rulesChannel = event.getGuild().getTextChannelById(ruleService.getChannelId());

        textChannelById.sendMessage(
                MessageCreateData.fromEmbeds(
                        new EmbedBuilder()
                                .setTitle("CookieCloudCloud | Welcome")
                                .setColor(Color.CYAN)
                                .setDescription(member.getUser().getAsMention() + " has joined the Discord!" +
                                        "\nPlease read our rules in " + rulesChannel.getAsMention() + "!")
                                .setFooter("Requested by " + member.getUser().getAsTag(), member.getUser().getEffectiveAvatarUrl())
                                .setImage("https://i.imgur.com/Mde2rcA.png")
                                .build()
                )
        ).queue(message -> {
            message.addReaction(Emoji.fromFormatted("⬆️")).queue();
            message.addReaction(Emoji.fromFormatted("⬇️")).queue();
        });
    }
}
