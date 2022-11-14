package de.cookiemc.driver.console.screen;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.console.TabCompleter;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Consumer;

public interface Screen {

    default void join() {
        CloudDriver.getInstance().getProviderRegistry().get(ScreenManager.class).ifPresent(sm -> sm.joinScreen(this));
    }

    default void leave() {
        CloudDriver.getInstance().getProviderRegistry().get(ScreenManager.class).ifPresent(ScreenManager::leaveCurrentScreen);
    }
    void writeLine(String line);

    void cacheLine(String line);

    Screen registerTabCompleter(TabCompleter completer);

    void suggestInput(String input);

    Collection<String> getHistory();

    void setHistory(Collection<String> history);

    TabCompleter getCurrentTabCompleter();

    Screen registerInputHandler(@Nonnull Consumer<? super String> handler);

    String getName();

    void setName(String name);

    void clear();

    void clearCache();

    @Nonnull
    Collection<Consumer<? super String>> getInputHandlers();

    Collection<String> getAllCachedLines();

    String readLineOrNull();
}
