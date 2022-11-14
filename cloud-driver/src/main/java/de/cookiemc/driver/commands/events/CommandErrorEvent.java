package de.cookiemc.driver.commands.events;

import de.cookiemc.driver.commands.data.DriverCommand;
import de.cookiemc.driver.commands.context.CommandContext;
import de.cookiemc.driver.commands.sender.CommandSender;
import de.cookiemc.driver.event.CloudEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CommandErrorEvent<T extends CommandSender> implements CloudEvent {

    private CommandContext<T> context;
    private T commandSender;
    private DriverCommand instance;
    private Throwable exception;

    @Setter
    private String message;

    public CommandErrorEvent(CommandContext<T> context, DriverCommand instance, Throwable exception) {
        this.context = context;
        this.commandSender = context.getCommandSender();
        this.instance = instance;
        this.exception = exception.getCause() != null ? exception.getCause() : exception;
        this.message = this.exception.getMessage();
    }

}
