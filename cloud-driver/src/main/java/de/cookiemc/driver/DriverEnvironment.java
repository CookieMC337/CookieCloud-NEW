package de.cookiemc.driver;

/**
 * The {@link DriverEnvironment} defines the Environment <br>
 * that a <b>{@link CloudDriver}</b> runs on
 * <br><br>
 * @author  CookieMC337
 * @since   SNAPSHOT-1.0
 */
public enum DriverEnvironment {

    /**
     * Should not be used
     */
    UNKNOWN,

    /**
     * The environment is a Node
     */
    NODE,

    /**
     * The Environment is a Remote
     */
    SERVICE

}
