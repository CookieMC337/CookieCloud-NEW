package de.cookiemc.driver.networking;

import de.cookiemc.driver.networking.protocol.SimpleNetworkComponent;
import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import de.cookiemc.driver.networking.protocol.packets.ConnectionType;

// TODO: 21.08.2022 documentation
public interface NetworkComponent extends IBufferObject {

    static NetworkComponent of(String name, ConnectionType type) {
        return new SimpleNetworkComponent(name, type);
    }

    @Deprecated
    static NetworkComponent of(String name) {
        return new SimpleNetworkComponent(name, ConnectionType.UNKNOWN);
    }

    String getName();

    ConnectionType getType();

    void log(String message, Object... args);

    default boolean matches(NetworkComponent component) {
        return getName().equalsIgnoreCase(component.getName()) && (getType() == component.getType() || getType() == ConnectionType.UNKNOWN);
    }
}
