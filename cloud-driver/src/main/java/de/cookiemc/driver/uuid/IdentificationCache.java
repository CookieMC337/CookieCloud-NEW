package de.cookiemc.driver.uuid;

import de.cookiemc.common.task.Task;

import java.util.Collection;
import java.util.UUID;

public interface IdentificationCache {

    boolean isEnabled();

    void setEnabled(boolean b);

    Task<Collection<UUID>> loadAsync();


    void setUUID(String name, UUID uuid);

    UUID getUUID(String name);

    void update();

    Collection<UUID> getCacheLoadedUniqueIds();

}
