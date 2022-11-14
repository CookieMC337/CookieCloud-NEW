package de.cookiemc.node.database.impl;

import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.task.Task;
import de.cookiemc.database.DatabaseDriver;
import de.cookiemc.database.DocumentDatabase;
import de.cookiemc.database.api.elements.Database;
import de.cookiemc.database.api.elements.DatabaseCollection;
import de.cookiemc.database.api.elements.DatabaseEntry;
import de.cookiemc.database.api.elements.DatabaseFilter;
import de.cookiemc.database.api.elements.def.DefaultDatabaseEntry;
import de.cookiemc.document.Document;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.http.HttpAddress;
import de.cookiemc.http.impl.NettyHttpServer;
import de.cookiemc.node.NodeDriver;


import de.cookiemc.driver.database.IDatabase;
import de.cookiemc.node.database.config.DatabaseConfiguration;

import java.io.File;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class DatabaseFile implements IDatabase {

    private final DatabaseDriver driver;
    private final DatabaseConfiguration configuration;
    private Database database;

    public DatabaseFile(DatabaseConfiguration configuration) {
        this.configuration = configuration;
        this.driver = new DatabaseDriver(CloudDriver.getInstance().getNetworkExecutor() == null ? "UNKNOWN" : CloudDriver.getInstance().getNetworkExecutor().getName());
        if (configuration.getPassword().equalsIgnoreCase("local") || configuration.getHost().equalsIgnoreCase("127.0.0.1")) {
            Task.runAsync(() -> {
                DocumentDatabase db = new DocumentDatabase(
                        Logger.constantInstance(),
                        NodeDriver.getInstance().getWebServer() == null ? new NettyHttpServer().addListener(new HttpAddress(configuration.getHost(), configuration.getPort())) : NodeDriver.getInstance().getWebServer(),
                        new File(NodeDriver.STORAGE_FOLDER, "database/"),
                        false,
                        configuration.getPassword()
                );

            });
        }
    }

    @Override
    public void connect() {
        Task.runAsync(() -> {
            this.driver.connect(new HttpAddress(configuration.getHost(), configuration.getPort()), configuration.getPassword());
            this.database = this.driver.getDatabase(configuration.getDatabase());
        });
    }

    @Override
    public void disconnect() {
        this.driver.close();
    }

    @Override
    public void insert(String collection, String key, Document document) {
        DatabaseCollection db = this.database.getCollectionOrCreate(collection);
        db.insertEntry(new DefaultDatabaseEntry(document, key));
    }

    @Override
    public void update(String collection, String key, Document document) {
        DatabaseCollection db = this.database.getCollectionOrCreate(collection);
        db.updateEntry(key, new DefaultDatabaseEntry(document, key));
    }

    @Override
    public boolean contains(String collection, String key) {
        DatabaseCollection db = this.database.getCollectionOrCreate(collection);
        return db.hasEntry(key);
    }

    @Override
    public void delete(String collection, String key) {
        DatabaseCollection db = this.database.getCollectionOrCreate(collection);
        db.deleteEntry(key);
    }

    @Override
    public Document byId(String collection, String key) {
        DatabaseCollection db = this.database.getCollectionOrCreate(collection);
        return db.findEntry(key);
    }

    @Override
    public Collection<Document> filter(String collection, String fieldName, Object fieldValue) {
        return documents(collection).stream().filter(d -> d.get(fieldName).toString().equalsIgnoreCase(fieldValue.toString())).collect(Collectors.toList());
    }

    @Override
    public Collection<Document> documents(String collection) {
        DatabaseCollection db = this.database.getCollectionOrCreate(collection);
        return db.filterEntriesAsync(DatabaseFilter.ALL).syncUninterruptedly().get().stream().map(e -> (Document)e).collect(Collectors.toList());
    }

    @Override
    public Map<String, Document> entries(String collection) {
        return filter(collection, (s, document) -> true);
    }

    @Override
    public Map<String, Document> filter(String collection, BiPredicate<String, Document> predicate) {
        DatabaseCollection db = this.database.getCollectionOrCreate(collection);
        Map<String, Document> map = new HashMap<>();
        for (DatabaseEntry entry : db.findEntries()) {
            if (predicate.test(entry.getId(), entry)) {
                map.put(entry.getId(), entry);
            }
        }
        return map;
    }

}
