package de.cookiemc.node.commands;

import de.cookiemc.driver.commands.AbstractCommandManager;

public class NodeCommandManager extends AbstractCommandManager {

    public NodeCommandManager() {
    }

    @Override
    public void handleCommandChange() {
        updateIngameCommands();
    }

}
