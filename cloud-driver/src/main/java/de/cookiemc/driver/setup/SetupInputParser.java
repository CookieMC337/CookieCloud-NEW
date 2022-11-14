package de.cookiemc.driver.setup;

public interface SetupInputParser<T> {

    T parse(SetupEntry entry, String input);
}
