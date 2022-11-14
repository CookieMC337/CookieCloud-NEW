package de.cookiemc.node.console;

import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.task.Task;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.ICommandManager;
import de.cookiemc.driver.commands.context.defaults.ConsoleCommandContext;
import de.cookiemc.driver.commands.data.DriverCommand;
import de.cookiemc.node.NodeDriver;

import java.util.function.Consumer;

public class NodeCommandInputHandler implements Consumer<String> {

    @Override
    public void accept(String s) {
        ICommandManager commandManager = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICommandManager.class);
        String[] args = s.split(" ");
        String command = args.length > 0 ? args[0] : s;

        if (!commandManager.hasCommand(command)) {

            DriverCommand similar = commandManager.getSimilarCommand(command);
            Logger.constantInstance().info((similar == null ?
                    "§cThis command is not known by the system! Use '§ehelp§c' for help." :
                    "§cWrong command! Did you mean '§e" + similar.getLabel() + "§c'?"));
        } else {
            Task.runAsync(() -> commandManager.executeCommand(args, new ConsoleCommandContext(NodeDriver.getInstance().getCommandSender())));
        }
    }
}
