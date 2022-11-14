package de.cookiemc.node.console;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.commands.events.TabCompleteEvent;
import de.cookiemc.driver.console.TabCompleter;
import de.cookiemc.driver.event.IEventManager;

import java.util.*;

public class NodeCommandCompleter implements TabCompleter {

    @Override
    public Collection<String> onTabComplete(String buffer) {

        Collection<String> result = new ArrayList<>();

        SortedSet<String> strings = new TreeSet<>();

        TabCompleteEvent event = new TabCompleteEvent(buffer);
        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(event);
        if (!event.isCancelled()) {
            Collection<String> l = event.getSuggestions();
            if (l != null) strings.addAll(l);
        }

        String currentBuffer = event.getCurrentBuffer();
        String add = event.getBeforeBuffer();

        if (currentBuffer == null) {
            for (String string : strings) {
                result.add(add + string);
            }
        } else {
            for (String match : strings.tailSet(currentBuffer)) {
                if (!match.startsWith(currentBuffer)) {
                    break;
                }
                result.add(add + match);
            }
        }

        return result;
    }
}
