package de.cookiemc.script.defaults;

import de.cookiemc.script.api.IScript;
import de.cookiemc.script.api.IScriptCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class DefaultPrintCommand implements IScriptCommand {

    @Override
    public String getCommand() {
        return "print";
    }

    @Override
    public void execute(@NotNull String commandLine, @NotNull IScript script, @NotNull Collection<String> allLines) {
        System.out.println(commandLine);
    }
}
