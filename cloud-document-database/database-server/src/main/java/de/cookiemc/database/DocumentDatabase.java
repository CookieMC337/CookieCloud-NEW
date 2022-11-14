package de.cookiemc.database;

import de.cookiemc.common.logging.LogLevel;
import de.cookiemc.common.logging.Logger;
import de.cookiemc.database.config.DatabaseConfig;
import de.cookiemc.database.handler.auth.AuthHandler;
import de.cookiemc.database.handler.other.data.ActionGetHandler;
import de.cookiemc.database.handler.other.database.GetDatabaseHandler;
import de.cookiemc.database.handler.other.data.ActionPostHandler;
import de.cookiemc.database.handler.other.document.GetDocumentHandler;
import de.cookiemc.database.handler.other.ConnectionPingHandler;
import de.cookiemc.database.handler.other.document.PostDocumentHandler;
import de.cookiemc.document.Document;
import de.cookiemc.http.api.HttpServer;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

@Getter
public class DocumentDatabase {

    @Getter
    private static DocumentDatabase instance;

    private final HttpServer webServer;
    private final DatabaseConfig config;
    private final File directory;

    public static final String EXTENSION = ".json";

    public DocumentDatabase(Logger logger, HttpServer webServer, File directory, boolean standalone, String... predefinedPassword) {
        instance = this;

        if (standalone) {

            logger.info("CookieCloudDatabase in its current State of Development is presented to you by Lystx...");

            logger.log(LogLevel.NULL, "§b                       _                    §f___ _                 _ ");
            logger.log(LogLevel.NULL, "§b           /\\  /\\_   _| |_ ___  _ __ __ _  §f/ __\\ | ___  _   _  __| |");
            logger.log(LogLevel.NULL, "§b          / /_/ / | | | __/ _ \\| '__/ _` |§f/ /  | |/ _ \\| | | |/ _` |");
            logger.log(LogLevel.NULL, "§b         / __  /| |_| | || (_) | | | (_| §f/ /___| | (_) | |_| | (_| |");
            logger.log(LogLevel.NULL, "§b         \\/ /_/  \\__, |\\__\\___/|_|  §f\\__,_\\____/|_|\\___/ \\__,_|\\__,_|");
            logger.log(LogLevel.NULL, "§b                 |___/                  §f                            ");
            logger.log(LogLevel.NULL, "            §8x §eDatabase V1 §8- §fDocument-based storage using Http §8x");
            logger.log(LogLevel.NULL, " ");
            logger.log(LogLevel.NULL, "           §8=>    §fJava Version " + System.getProperty("java.version"));
            logger.log(LogLevel.NULL, "           §8=>    §fDiscord " + "https://discord.com/invite/WRYH33X7Fu");
            logger.log(LogLevel.NULL, "");
            logger.info("§8");
        }
        logger.info("Loading Document-Database-Driver...");
        this.directory = directory;
        this.directory.mkdirs();

        this.webServer = webServer;
        this.config = loadConfig(standalone, predefinedPassword.length == 0 ? null : predefinedPassword[0]);

        logger.info("Loaded Document-Database-Config!");
        this.webServer.getAuthRegistry().registerAuthMethodHandler(new AuthHandler());
        this.webServer.getHandlerRegistry().registerHandler("database", new GetDocumentHandler());
        this.webServer.getHandlerRegistry().registerHandler("database", new ConnectionPingHandler());
        this.webServer.getHandlerRegistry().registerHandler("database", new PostDocumentHandler());
        this.webServer.getHandlerRegistry().registerHandler("database", new GetDatabaseHandler());
        this.webServer.getHandlerRegistry().registerHandler("database", new ActionPostHandler());
        this.webServer.getHandlerRegistry().registerHandler("database", new ActionGetHandler());

        logger.info("Loaded " + this.webServer.getHandlerRegistry().getHandlers().size() + " WebHandlers!");
    }

    @Nonnull
    private DatabaseConfig loadConfig(boolean standalone, String password) {
        if (!standalone) {
            return new DatabaseConfig(password);
        }
        File configFile = new File("config" + EXTENSION);
        if (!configFile.exists()) {
            try {
                Document.newJsonDocument(new DatabaseConfig(password)).saveToFile(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                return Document.newJsonDocument(configFile).toInstance(DatabaseConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new DatabaseConfig();
    }
}
