package de.cookiemc.driver.setup.suggesters;

import de.cookiemc.driver.setup.Setup;
import de.cookiemc.driver.setup.SetupEntry;
import de.cookiemc.driver.setup.SetupSuggester;

import java.util.Arrays;
import java.util.List;

public class BooleanSuggester implements SetupSuggester {
    @Override
    public List<String> suggest(Setup<?> setup, SetupEntry entry) {
        return Arrays.asList("true", "false");
    }
}
