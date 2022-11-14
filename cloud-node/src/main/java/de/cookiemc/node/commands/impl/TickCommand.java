package de.cookiemc.node.commands.impl;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.context.CommandContext;
import de.cookiemc.driver.commands.data.Command;
import de.cookiemc.driver.commands.data.enums.CommandScope;
import de.cookiemc.driver.commands.parameter.CommandArguments;
import de.cookiemc.driver.tps.TickCounter;
import de.cookiemc.driver.tps.TickType;
import de.cookiemc.driver.tps.ICloudTickWorker;

public class TickCommand {

    @Command(
            label = "tps",
            aliases = "ticks",
            desc = "Shows performance of cloud",
            permission = "cloud.command.tick",
            scope = CommandScope.CONSOLE_AND_INGAME
    )
    public void execute(CommandContext<?> ctx, CommandArguments args) {

        String s = args.get(0, String.class);
        ctx.sendMessage("§8");
        ctx.sendMessage("§7Tps§8:");
        for (TickType type : TickType.values()) {
            TickCounter tick = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICloudTickWorker.class).getTick(type);
            double tps = tick.getAverage();
            ctx.sendMessage("§b" + type.getLabel() + ": §7" + tps);
        }
        ctx.sendMessage("§8");
    }
}
