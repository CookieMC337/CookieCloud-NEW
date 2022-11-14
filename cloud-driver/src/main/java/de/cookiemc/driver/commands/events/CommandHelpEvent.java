package de.cookiemc.driver.commands.events;

import de.cookiemc.driver.commands.context.CommandContext;
import de.cookiemc.driver.commands.sender.CommandSender;
import de.cookiemc.driver.event.Cancelable;
import de.cookiemc.driver.event.CloudEvent;
import lombok.Getter;

/**
 * This event is for sending help about one specific command, that means the
 * user used the '-?' flag<br>
 * Cancelling this event means you've handled the event successfully
 */
@Getter
public class CommandHelpEvent<T extends CommandSender> implements CloudEvent, Cancelable {

    private CommandContext<T> context;
    private boolean cancelled = false;
    private boolean onlyDescription;

    public CommandHelpEvent(CommandContext<T> context, boolean onlyDescription) {
        this.context = context;
        this.onlyDescription = onlyDescription;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
