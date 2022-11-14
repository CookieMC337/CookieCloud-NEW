package de.cookiemc.driver.setup;

import java.util.List;

public interface SetupSuggester {


    List<String> suggest(Setup<?> setup, SetupEntry entry);
}
