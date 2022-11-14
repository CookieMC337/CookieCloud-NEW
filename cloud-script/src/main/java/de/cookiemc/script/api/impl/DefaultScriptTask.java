package de.cookiemc.script.api.impl;

import de.cookiemc.script.ScriptSyntax;
import de.cookiemc.script.api.IScript;
import de.cookiemc.script.api.IScriptCommand;
import de.cookiemc.script.api.IScriptDecision;
import de.cookiemc.script.api.IScriptTask;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
public class DefaultScriptTask implements IScriptTask {

    private final String name;
    private final Map<String, IScriptCommand> commands;
    private final Map<String, IScriptDecision> decisionsPerLine;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void executeTask(String callerLine, IScript script, Collection<String> allLines) {

        for (String line : allLines) {
            //System.out.println("Executing script line " + line);

            IScriptCommand entry = this.commands.get(line.trim());
            if (entry == null) {
                IScriptDecision decision = this.decisionsPerLine.get(line.trim());
                if (decision != null) {
                    if (decision.getChecker().supply(script)) {
                        decision.executeTrue(script);
                    } else {
                        decision.executeFalse(script);
                    }
                }
                continue;
            }

            entry.execute(ScriptSyntax.formatCommandLine(script, entry, line.trim()), script, allLines);
        }

        /*
        for (Map.Entry<String, IScriptCommand> commandEntry : this.commands.entrySet()) {
            String key = commandEntry.getKey();
            IScriptCommand command = commandEntry.getValue();

            command.execute(ScriptSyntax.formatCommandLine(script, command, key), script, allLines);
        }*/
    }
}