package de.cookiemc.database;

import de.cookiemc.common.task.Task;
import de.cookiemc.database.api.IPayLoad;
import de.cookiemc.database.api.elements.Database;
import de.cookiemc.database.api.elements.def.HttpDatabase;
import de.cookiemc.database.http.HttpDriver;
import de.cookiemc.document.Bundle;
import de.cookiemc.document.Document;
import de.cookiemc.http.HttpAddress;
import de.cookiemc.http.api.HttpMethod;
import lombok.Getter;

import java.util.Collection;

public class DatabaseDriver {

    @Getter
    private static DatabaseDriver instance;

    private final String username;

    public DatabaseDriver(String username) {
        instance = this;

        this.username = username;
    }

    public Task<IPayLoad> connect(HttpAddress address, String token) {
        Task<IPayLoad> promise = Task.empty();
        HttpDriver driver = new HttpDriver(token, address);

        driver.sendRequestAsync(
                        "connection/ping",
                        HttpMethod.POST,
                        Document.newJsonDocument(
                                "name", this.username,
                                "address", HttpAddress.currentPublicIp().toString()
                        )
                ).onTaskSucess(promise::setResult)
                .onTaskFailed(promise::setFailure);

        return promise;
    }

    public Task<IPayLoad> close() {
        Task<IPayLoad> promise = Task.empty();

        HttpDriver.getInstance().sendRequestAsync(
                        "connection/close",
                        HttpMethod.POST,
                        Document.newJsonDocument(
                                "name", this.username,
                                "address", HttpAddress.currentPublicIp().toString()
                        )
                ).onTaskSucess(promise::setResult)
                .onTaskFailed(promise::setFailure);

        return promise;
    }

    public Database getDatabase(String name) {
        return new HttpDatabase(name);
    }

    public Collection<Database> getDatabases() {
        Bundle bundle = HttpDriver
                .getInstance()
                .sendRequest("database/findAll", HttpMethod.GET)
                .toBundle();
        return bundle.map(entry -> getDatabase(entry.toString()));
    }
}
