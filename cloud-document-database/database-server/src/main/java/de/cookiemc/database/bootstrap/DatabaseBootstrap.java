package de.cookiemc.database.bootstrap;

import de.cookiemc.common.logging.LogLevel;
import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.logging.formatter.UncoloredMessageFormatter;
import de.cookiemc.common.logging.handler.HandledAsyncLogger;
import de.cookiemc.database.DocumentDatabase;
import de.cookiemc.http.HttpAddress;
import de.cookiemc.http.api.HttpServer;
import de.cookiemc.http.impl.NettyHttpServer;

import java.io.File;
import java.io.PrintStream;

public class DatabaseBootstrap {

    public static void main(String[] args) {

        HandledAsyncLogger logger = new HandledAsyncLogger(LogLevel.DEBUG);
        logger.addHandler(entry -> {

            PrintStream stream = entry.getLevel() == LogLevel.ERROR ? System.err : System.out;
            stream.println(UncoloredMessageFormatter.format(entry));
        });
        Logger.setFactory(logger);

        HttpServer webServer = new NettyHttpServer();
        webServer.addListener(new HttpAddress("127.0.0.1", 8890));

        new DocumentDatabase(
                logger,
                webServer,
                new File("database/"),
                true
        );
    }
}
