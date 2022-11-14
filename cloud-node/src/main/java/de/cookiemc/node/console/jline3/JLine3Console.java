package de.cookiemc.node.console.jline3;

import de.cookiemc.common.VersionInfo;
import de.cookiemc.common.logging.LogLevel;
import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.logging.formatter.ColoredMessageFormatter;
import de.cookiemc.common.logging.formatter.SpacePadder;
import de.cookiemc.common.logging.handler.LogEntry;
import de.cookiemc.common.misc.CollectionUtils;
import de.cookiemc.common.misc.ReflectionUtils;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.ICommandManager;
import de.cookiemc.driver.commands.context.CommandContext;
import de.cookiemc.driver.commands.data.Command;
import de.cookiemc.driver.commands.data.DriverCommand;
import de.cookiemc.driver.commands.parameter.CommandArguments;
import de.cookiemc.driver.console.Console;
import de.cookiemc.driver.console.screen.Screen;
import de.cookiemc.driver.console.screen.ScreenManager;
import de.cookiemc.node.NodeDriver;
import de.cookiemc.node.console.ColorTranslator;
import lombok.Getter;
import lombok.Setter;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
public class JLine3Console implements Console, Consumer<String> {

    private final ConsoleReadThread consoleReadThread = new ConsoleReadThread(this);
    private final Collection<Consumer<? super String>> inputHandlers = new ArrayList<>();
    private final List<String> allWroteLines = new ArrayList<>();

    private final Terminal terminal;
    private final LineReaderImpl lineReader;

    private final String promptTemplate;
    private String prompt;
    private String screenName = "Console";

    public JLine3Console(String promptTemplate) throws Exception {
        System.setProperty("library.jansi.version", "CookieCloud");
        this.promptTemplate = promptTemplate;
        this.prompt = promptTemplate;

        //installing ansi
        AnsiConsole.systemInstall();

        //adding input handler for screen
        this.addInputHandler(this);

        this.terminal = TerminalBuilder.builder().system(true).encoding(StandardCharsets.UTF_8).build();
        this.lineReader = new CustomLineReader(terminal, "HC-Console");

        //copied from CloudNet Console part
        this.lineReader.setAutosuggestion(LineReader.SuggestionType.COMPLETER);
        this.lineReader.setCompleter(new JLine3Completer());

        //copied from CloudNet Console part
        this.lineReader.option(LineReader.Option.AUTO_GROUP, false);
        this.lineReader.option(LineReader.Option.AUTO_MENU_LIST, true);
        this.lineReader.option(LineReader.Option.AUTO_FRESH_LINE, true);
        this.lineReader.option(LineReader.Option.EMPTY_WORD_OPTIONS, false);
        this.lineReader.option(LineReader.Option.HISTORY_TIMESTAMPED, false);
        this.lineReader.option(LineReader.Option.DISABLE_EVENT_EXPANSION, true);

        //copied from CloudNet Console part
        this.lineReader.variable(LineReader.BELL_STYLE, "none");
        this.lineReader.variable(LineReader.HISTORY_SIZE, 500);
        this.lineReader.variable(LineReader.HISTORY_FILE_SIZE, 2500);
        this.lineReader.variable(LineReader.COMPLETION_STYLE_LIST_BACKGROUND, "inverse");

        this.updatePrompt();
        consoleReadThread.start();
    }



    @Override
    public void setCommandInputValue(@Nonnull String commandInputValue) {
        lineReader.getBuffer().write(commandInputValue);
    }


    @Override
    public String readLineOrNull() {
        return consoleReadThread.readLineOrNull();
    }


    @NotNull
    @Override
    public Console writeLine(@NotNull String text) {
        if (!text.endsWith(System.lineSeparator()) && !text.startsWith("\r")) {
            text += System.lineSeparator();
        }
        ScreenManager sm = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ScreenManager.class);
        Screen console = sm.getScreenByNameOrNull("console");
        if (console != null) {
            console.cacheLine(text);
        }
        if (sm.isScreenActive("console")) {
            forceWrite(text);
        }
        return this;
    }

    @Override
    public Console writeEntry(@NotNull LogEntry entry) {
        String formatted = ColoredMessageFormatter.format(entry);

        if (!formatted.endsWith(System.lineSeparator()) && !entry.getMessage().startsWith("\r")) {
            formatted += System.lineSeparator();
        }
        if (entry.getMessage().startsWith("\r")) {
            formatted = "\r" + formatted;
        }
        ScreenManager sm = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ScreenManager.class);
        Screen console = sm.getScreenByNameOrNull("Console");
        if (console != null) {
            console.cacheLine(entry.getMessage());
        }
        if (sm.isScreenActive("console")) {
            forceWrite(formatted);
        }
        return this;
    }

    @Override
    public Console forceWrite(String text) {
        if (!text.endsWith(System.lineSeparator()) && !text.startsWith("\r")) {
            text += System.lineSeparator();
        }
        if (text.startsWith("\r")) {
            print(text);
            return this;
        }
        print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + '\r' + text + Ansi.ansi().reset().toString());
        return this;
    }


    @Override
    public Console writePlain(String text) {
        print(text);
        return this;
    }



    @Override
    public void resetPrompt() {
        prompt = promptTemplate;
        updatePrompt();
    }

    @Override
    public void setCommandHistory(List<String> history) {
        DefaultHistory entries = new DefaultHistory();
        for (String s : history) {
            entries.add(s);
        }
        lineReader.setHistory(entries);
    }

    @Override
    public Collection<String> getCommandHistory() {
        Collection<String> history = new ArrayList<>();

        for (History.Entry entry : lineReader.getHistory()) {
            history.add(entry.line());
        }

        return history;
    }

    @Override
    public void clearScreen() {
        ReflectionUtils.clearConsole();
        this.redisplay();
    }

    @Override
    public void close() throws Exception {
        consoleReadThread.interrupt();

        terminal.flush();
        terminal.close();

        AnsiConsole.systemUninstall();
    }

    @Nonnull
    @Override
    public String getPrompt() {
        return prompt;
    }

    @Override
    public void setPrompt(@Nonnull String prompt) {
        this.prompt = prompt;
        updatePrompt();
    }

    @Override
    public void setScreenName(@Nonnull String screenName) {
        this.screenName = screenName;
        resetPrompt();
    }

    @Override
    public void addInputHandler(@Nonnull Consumer<? super String> handler) {
        inputHandlers.add(handler);
    }

    @Nonnull
    @Override
    public Collection<Consumer<? super String>> getInputHandlers() {
        return new ArrayList<>(inputHandlers);
    }

    @Override
    public void printHeader() {

        Logger logger = Logger.constantInstance();

        logger.info("CookieCloud in its current State of Development is presented to you by CookieMC337");
        logger.log(LogLevel.NULL, "§b _____             _    _      _____ _                 _ ");
        logger.log(LogLevel.NULL, "§b/  __ \\           | |  (_)    /  __ \\ |               | |");
        logger.log(LogLevel.NULL, "§b| /  \\/ ___   ___ | | ___  ___| /  \\/ | ___  _   _  __| |");
        logger.log(LogLevel.NULL, "§b| |    / _ \\ / _ \\| |/ / |/ _ \\ |   | |/ _ \\| | | |/ _` |");
        logger.log(LogLevel.NULL, "§b| \\__/\\ (_) | (_) |   <| |  __/ \\__/\\ | (_) | |_| | (_| |");
        logger.log(LogLevel.NULL, "§b \\____/\\___/ \\___/|_|\\_\\_|\\___|\\____/_|\\___/ \\__,_|\\__,_|");
        logger.log(LogLevel.NULL, " ");
        logger.log(LogLevel.NULL, " ");
        logger.log(LogLevel.NULL, "           §bThis is a §e§lBETA §bVersion of CookieCloud");
        logger.log(LogLevel.NULL, "           §8=>    §fCloud Version " + VersionInfo.getCurrentVersion());
        logger.log(LogLevel.NULL, "           §8=>    §fJava Version " + System.getProperty("java.version"));
        logger.log(LogLevel.NULL, "           §8=>    §fDiscord " + "https://discord.com/invite/WRYH33X7Fu");
        logger.log(LogLevel.NULL, "");
        logger.info("§8");
    }

    private void updatePrompt() {
        prompt = ColorTranslator.translate(prompt)
                .replace("%node%", NodeDriver.getInstance() == null ? "Node" : (NodeDriver.getInstance().getNode() == null ? "Node" : NodeDriver.getInstance().getNode().getName()))
                .replace("%screen%", screenName);
        lineReader.setPrompt(prompt);
    }

    private void print(@Nonnull String text) {
        this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
        this.lineReader.getTerminal().puts(InfoCmp.Capability.clr_eol);
        this.lineReader.getTerminal().writer().print(text);
        this.lineReader.getTerminal().writer().flush();
        this.redisplay();
    }

    private void redisplay() {
        if (!lineReader.isReading()) {
            return;
        }

        lineReader.callWidget(LineReader.REDRAW_LINE);
        lineReader.callWidget(LineReader.REDISPLAY);
    }

    @Nonnull
    public LineReader getLineReader() {
        return lineReader;
    }

    @Override
    public void accept(String s) {
        ScreenManager sm = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ScreenManager.class);
        if (sm.isCurrentlyInScreen()) {
            Screen screen = sm.getCurrentScreen();
            if (screen == null) {
                return;
            }
            for (Consumer<? super String> handler : new ArrayList<>(screen.getInputHandlers())) {
                handler.accept(s);
            }
        }
    }


    @Command(label = "help", aliases = {"?"}, usage = "[page]")
    public void helpCommand(CommandContext<?> context, CommandArguments args) {

        Integer page = args.getInt(0, 1);


        Collection<String> duplicates = new ArrayList<>();
        List<DriverCommand> commands = new ArrayList<>();

        for (DriverCommand command : CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICommandManager.class).getRootCommands().stream().sorted(Comparator.comparing(DriverCommand::getPath)).collect(Collectors.toList())) {
            if (!command.getCommandScope().covers(context.getCommandSender())) {
                continue;
            }
            if (duplicates.stream().anyMatch(s -> command.getNames().contains(s))) { //to avoid aliases create conflicts
                continue;
            }
            duplicates.addAll(command.getNames());
            commands.add(command);

        }

        //split command help after 5 command entries
        List<List<DriverCommand>> splitCommands = CollectionUtils.splitCollection(commands, 5);
        try {
            List<DriverCommand> pagedCommands = splitCommands.get((page - 1));

            context.sendMessage("§8");
            context.sendMessage("§6=> CommandHelp Page §8[§b{}§8/§b{}§8]§8:", page, splitCommands.size());
            context.sendMessage("§8");
            for (DriverCommand command : pagedCommands) {

                StringBuilder builder = new StringBuilder();

                int triggerLength = 22;
                int permissionLength = 25;
                int descriptionLength = 25;

                String triggers = command.getNames().toString();
                String permission = (command.getPermission() == null || command.getPermission().trim().isEmpty()) ? "None" : command.getPermission();
                String description = (!command.getDescription().trim().isEmpty() ? command.getDescription() : "No Desc");


                if (triggers.length() > triggerLength) triggers = triggers.substring(triggers.length() - triggerLength);
                if (permission.length() > permissionLength) permission = permission.substring(permission.length() - permissionLength);
                if (description.length() > descriptionLength) description = description.substring(description.length() - descriptionLength);

                //command triggers
                builder.append("  §8» §7Trigger§8: §b");
                SpacePadder.padRight(builder, triggers, triggerLength);
                builder.append(" ");

                //command permission
                builder.append("§8| §7Perm§8: §b");
                SpacePadder.padRight(builder, permission, permissionLength);
                builder.append(" ");

                //command description
                builder.append("§8| §f");
                SpacePadder.padRight(builder, description, descriptionLength);

                context.sendMessage(builder.toString());
            }

            context.sendMessage("§8");
            if (page < splitCommands.size()) {
                context.sendMessage("§a=> §7Next Page§8: §ehelp {}", (page + 1));
            } else {
                context.sendMessage("§c=> §7Previous Page§8: §ehelp {}", (page - 1));
            }
            context.sendMessage("§8");
        } catch (Exception e) {
            context.sendMessage("§cThere is no page with index §e" + page + "§c!");
        }

    }
}