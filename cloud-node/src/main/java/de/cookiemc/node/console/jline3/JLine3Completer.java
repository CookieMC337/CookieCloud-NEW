package de.cookiemc.node.console.jline3;


import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.console.TabCompleter;
import de.cookiemc.driver.console.screen.ScreenManager;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.stream.Collectors;

public class JLine3Completer implements Completer {

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {

        String buffer = line.line();
        ScreenManager sm = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ScreenManager.class);
        if (sm.isCurrentlyInScreen()) {
            TabCompleter tc = sm.getCurrentScreen().getCurrentTabCompleter();
            if (tc != null) {
                candidates.addAll(tc.onTabComplete(buffer).stream().map(Candidate::new).collect(Collectors.toList()));
            }
        }
    }
}
