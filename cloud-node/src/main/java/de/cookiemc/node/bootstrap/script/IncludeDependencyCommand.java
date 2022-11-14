package de.cookiemc.node.bootstrap.script;

import de.cookiemc.node.bootstrap.library.Dependency;
import de.cookiemc.script.api.IScript;
import de.cookiemc.script.api.IScriptCommand;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;

@Data
public class IncludeDependencyCommand implements IScriptCommand {

    private final Consumer<Dependency> includer;

    @Override
    public String getCommand() {
        return "dependency";
    }

    @Override
    public void execute(@NotNull String commandLine, @NotNull IScript script, @NotNull Collection<String> allLines) {
        String[] args = commandLine.split(" ");
        if (args.length >= 3) {
            Dependency dependency = new Dependency(args[0], args[1], args[2], args.length == 4 ? args[3] : "mvn");
            this.includer.accept(dependency);
        }
    }
}
