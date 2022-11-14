package de.cookiemc.driver.setup.suggesters;

import de.cookiemc.driver.setup.annotations.RequiresEnum;
import de.cookiemc.driver.setup.Setup;
import de.cookiemc.driver.setup.SetupEntry;
import de.cookiemc.driver.setup.SetupSuggester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumSuggester implements SetupSuggester {
    
    @Override
    public List<String> suggest(Setup<?> setup, SetupEntry entry) {
        RequiresEnum requiresEnum = entry.getRequiresEnum();
        if (requiresEnum == null) {
            return new ArrayList<>();
        }
        Class<? extends Enum<?>> value = requiresEnum.value();
        return Arrays.stream(value.getEnumConstants()).map(Enum::name).collect(Collectors.toList());
    }
}
