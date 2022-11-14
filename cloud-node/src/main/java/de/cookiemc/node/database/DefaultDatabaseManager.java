package de.cookiemc.node.database;

import de.cookiemc.common.task.Task;


import de.cookiemc.driver.database.SectionedDatabase;
import de.cookiemc.node.database.config.DatabaseConfiguration;
import de.cookiemc.node.database.config.DatabaseType;
import de.cookiemc.driver.database.IDatabase;
import de.cookiemc.driver.database.IDatabaseManager;
import de.cookiemc.node.database.impl.DatabaseFile;
import de.cookiemc.node.database.impl.DatabaseMongoDB;
import de.cookiemc.node.database.impl.DatabaseMySQL;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class DefaultDatabaseManager implements IDatabaseManager {

    private final IDatabase internalDatabase;
    private SectionedDatabase database;

    public DefaultDatabaseManager(DatabaseType type, DatabaseConfiguration configuration) {
        if (type == DatabaseType.MYSQL) {
            this.internalDatabase = new DatabaseMySQL(configuration);
        } else if (type == DatabaseType.MONGODB) {
            this.internalDatabase = new DatabaseMongoDB(configuration);
        } else {
            this.internalDatabase = new DatabaseFile(configuration);
        }
        //database cannot be null
        this.internalDatabase.connect();
        this.database = new SectionedDatabase(this.internalDatabase);
    }

    @Override
    public @NotNull Task<Boolean> shutdown() {
        internalDatabase.disconnect();
        return Task.newInstance(true);
    }


}
