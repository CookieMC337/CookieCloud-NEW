package de.cookiemc.node.commands.impl;

import de.cookiemc.common.scheduler.Scheduler;
import de.cookiemc.driver.commands.context.CommandContext;
import de.cookiemc.driver.commands.data.Command;
import de.cookiemc.driver.commands.data.enums.CommandScope;
import de.cookiemc.driver.commands.help.CommandHelp;
import de.cookiemc.driver.commands.help.CommandHelper;
import de.cookiemc.driver.commands.parameter.CommandArguments;
import de.cookiemc.driver.commands.tabcomplete.TabCompleter;
import de.cookiemc.driver.commands.tabcomplete.TabCompletion;
import de.cookiemc.driver.node.INode;
import de.cookiemc.driver.node.INodeManager;
import de.cookiemc.node.NodeDriver;

@Command(
        label = "cluster",
        aliases = "cl",
        desc = "Manages the cluster",
        invalidUsageIfEmptyInput = true,
        autoHelpAliases = {"help", "?"},
        permission = "cloud.command.cluster",
        scope = CommandScope.CONSOLE_AND_INGAME
)
// TODO: 21.08.2022 implement more
public class ClusterCommand {

    @CommandHelp
    public void onArgumentHelp(CommandHelper helper) {
        helper.performTemplateHelp();
    }

    @TabCompletion
    public void onTabComplete(TabCompleter completer) {
        completer.reactWithSubCommands("cluster");
    }

    @Command(
            label = "shutdown",
            parent = "cluster",
            desc = "Shuts down the cluster",
            scope = CommandScope.CONSOLE_AND_INGAME
    )
    public void shutdownCommand(CommandContext<?> ctx, CommandArguments args) {
        ctx.sendMessage("Sending Shutdown-Request to every ClusterParticipant and then shutting down HeadNode after 1 second...");
        INode headNode = NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).getHeadNode();
        for (INode node: NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).getAllCachedNodes()) {
            if (node.getName().equalsIgnoreCase(headNode.getName())) {
                continue; //headNode can't be shut down before all other nodes are shut down
            }
            node.shutdown();
        }
        Scheduler.runTimeScheduler().scheduleDelayedTask(headNode::shutdown, 20L);
        if (NodeDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).isHeadNode()) {
            return;
        }
        Scheduler.runTimeScheduler().scheduleDelayedTask(() -> NodeDriver.getInstance().shutdown(), 40L);
    }


}
