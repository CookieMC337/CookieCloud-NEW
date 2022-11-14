package de.cookiemc.driver.database;

import de.cookiemc.common.task.Task;
import org.jetbrains.annotations.NotNull;

public interface IDatabaseManager {

    @NotNull
    IDatabase getInternalDatabase();

    SectionedDatabase getDatabase();

    @NotNull
    Task<Boolean> shutdown();

}
