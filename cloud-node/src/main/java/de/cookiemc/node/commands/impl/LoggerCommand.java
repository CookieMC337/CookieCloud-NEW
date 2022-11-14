package de.cookiemc.node.commands.impl;

import de.cookiemc.common.logging.LogLevel;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.data.Command;
import de.cookiemc.driver.commands.context.CommandContext;
import de.cookiemc.driver.commands.data.enums.CommandScope;
import de.cookiemc.driver.commands.help.CommandHelp;
import de.cookiemc.driver.commands.help.CommandHelper;
import de.cookiemc.driver.commands.parameter.CommandArguments;
import de.cookiemc.driver.commands.tabcomplete.TabCompletion;
import de.cookiemc.driver.commands.tabcomplete.TabCompleter;
import de.cookiemc.node.NodeDriver;
import de.cookiemc.node.config.MainConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LoggerCommand {

    @CommandHelp
    public void help(CommandHelper<?> helper) {
        helper.setResult(
                "§cUse §elogger <LogLevel>!",
                "§cAvailable types: " + Arrays.stream(LogLevel.values()).map(LogLevel::name).collect(Collectors.toList()).toString().replace("[", "").replace("]", "")
        );
    }

    @TabCompletion
    public void tabComplete(TabCompleter completer) {
        completer.setResult(1, Arrays.stream(LogLevel.values()).map(LogLevel::name).collect(Collectors.toList()));
    }

    @Command(
            label = "logger",
            usage = "[level]",
            flags = "?[]",
            permission = "cloud.command.logger",
            scope = CommandScope.CONSOLE,
            desc = "Changes the LogLevel at Runtime!"
    )
    public void execute(CommandContext<?> context, CommandArguments args) {

        if (args.size() != 1) {
            context.sendHelp();
            return;
        }
        LogLevel level = LogLevel.fromName(args.getString(0, "INFO"));

        try {
            MainConfiguration instance = MainConfiguration.getInstance();
            instance.setLogLevel(level);

            NodeDriver.getInstance().getConfigManager().setConfig(instance);
            NodeDriver.getInstance().getConfigManager().save();


            CloudDriver.getInstance().getLogger().setMinLevel(level);
            context.sendMessage("Changed LogLevel to {}", level);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
