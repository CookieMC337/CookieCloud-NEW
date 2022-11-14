package de.cookiemc.node.setup.database;

import de.cookiemc.driver.console.Console;
import de.cookiemc.driver.setup.Setup;
import de.cookiemc.driver.setup.annotations.Question;
import lombok.Getter;

@Getter
public class MySqlSetup extends Setup<MySqlSetup> {

    @Question(id = 1, question = "What's the host of your database?")
    private String databaseHost;

    @Question(id = 2, question = "What's the port of your database?")
    private int databasePort;

    @Question(id = 3, question = "What's the username of your database?")
    private String databaseUser;

    @Question(id = 4, question = "What's the password of your database?")
    private String databasePassword;

    @Question(id = 5, question = "What's the name of your database?")
    private String databaseName;

    public MySqlSetup(Console console) {
        super();
    }


    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public boolean shouldPrintHeader() {
        return true;
    }

}
