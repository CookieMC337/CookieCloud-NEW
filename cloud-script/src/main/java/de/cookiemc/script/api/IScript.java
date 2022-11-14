package de.cookiemc.script.api;

import de.cookiemc.common.task.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public interface IScript {

    void putVariable(@NotNull String name, @NotNull String value);

    @Nullable
    String getVariable(@NotNull String name);

    @NotNull
    Map<String, String> getVariables();

    @NotNull
    String replaceVariables(@NotNull  String line);

    @NotNull
    IScriptLoader getLoader();


    @NotNull
    Collection<IScriptTask> getAllTasks();

    @NotNull
    Path getScriptPath();

    Task<Void> executeAsync();

    void execute();
}
