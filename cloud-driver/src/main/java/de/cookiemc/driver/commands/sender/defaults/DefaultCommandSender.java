package de.cookiemc.driver.commands.sender.defaults;

import de.cookiemc.common.logging.Logger;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.DriverEnvironment;
import de.cookiemc.driver.commands.sender.ConsoleCommandSender;
import de.cookiemc.driver.common.CloudMessages;
import de.cookiemc.driver.component.style.ComponentColor;
import de.cookiemc.driver.console.Console;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Getter @RequiredArgsConstructor
public class DefaultCommandSender implements ConsoleCommandSender {

    private final String name;
    private final Console console;

    /**
     * The consumer to send a message
     */
    private Consumer<String> messageSending;

    /**
     * The consumer to send a message
     */
    private Consumer<String> forceSending;
    @Override
    public void sendMessage(@NotNull String message) {
        if (CloudDriver.getInstance().getEnvironment() == DriverEnvironment.NODE) {
            message = ComponentColor.translateColorCodes('§', message);
            CloudMessages messages = CloudMessages.retrieveFromStorage();
            if (messages != null) {
                message = message.replace(messages.getPrefix(), "");
            }
        }
        if (messageSending != null) {
            messageSending.accept(message);
            return;
        }
        getLogger().info(message);
    }

    @Override
    public void forceMessage(@NotNull String message) {
        this.forceSending.accept(message);
    }

    public DefaultCommandSender function(Consumer<String> messageSending) {
        this.messageSending = messageSending;
        return this;
    }

    public DefaultCommandSender forceFunction(Consumer<String> forceSending) {
        this.forceSending = forceSending;
        return this;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }

    @NotNull
    @Override
    public Logger getLogger() {
        return CloudDriver.getInstance().getLogger();
    }
}
