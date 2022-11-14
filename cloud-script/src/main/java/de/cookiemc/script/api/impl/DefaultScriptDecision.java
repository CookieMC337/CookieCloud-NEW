package de.cookiemc.script.api.impl;

import de.cookiemc.common.function.BiSupplier;
import de.cookiemc.script.ScriptSyntax;
import de.cookiemc.script.api.IScript;
import de.cookiemc.script.api.IScriptCommand;
import de.cookiemc.script.api.IScriptDecision;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Data
public class DefaultScriptDecision implements IScriptDecision {

    private BiSupplier<IScript, Boolean> checker;

    private final Map<String, IScriptCommand> trueCommands = new HashMap<>();
    private final Map<String, IScriptCommand> falseCommands = new HashMap<>();

    @Override
    public void executeFalse(IScript script) {

        for (Map.Entry<String, IScriptCommand> commandEntry : this.falseCommands.entrySet()) {
            String key = commandEntry.getKey();
            IScriptCommand command = commandEntry.getValue();

            command.execute(ScriptSyntax.formatCommandLine(script, command, key), script, new ArrayList<>());
        }
    }


    @Override
    public void executeTrue(IScript script) {

        for (Map.Entry<String, IScriptCommand> commandEntry : this.trueCommands.entrySet()) {
            String key = commandEntry.getKey();
            IScriptCommand command = commandEntry.getValue();

            command.execute(ScriptSyntax.formatCommandLine(script, command, key), script, new ArrayList<>());
        }
    }
}
