package de.cookiemc.database.config;

import de.cookiemc.common.misc.RandomString;
import lombok.Getter;

@Getter
public class DatabaseConfig {

    private final String token;

    public DatabaseConfig() {
        this(null);
    }
    public DatabaseConfig(String token) {
        this.token = token == null ? new RandomString(10).nextString() : token;
    }
}
