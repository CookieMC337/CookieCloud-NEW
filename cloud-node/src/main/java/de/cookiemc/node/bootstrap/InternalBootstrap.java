package de.cookiemc.node.bootstrap;

import de.cookiemc.common.logging.LogLevel;
import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.logging.handler.HandledAsyncLogger;
import de.cookiemc.common.logging.handler.HandledLogger;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.console.Console;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.driver.DriverLogEvent;
import de.cookiemc.driver.node.INode;
import de.cookiemc.node.NodeDriver;
import de.cookiemc.node.console.handler.ConsoleLogHandler;
import de.cookiemc.node.console.handler.FileLogHandler;
import de.cookiemc.node.console.jline3.JLine3Console;

import java.util.Arrays;

public class InternalBootstrap {

    public static void main(String[] args) {

        try {
            Console console = new JLine3Console("§c%node%@§f%screen% §8» §r");
            HandledLogger logger = new HandledAsyncLogger(LogLevel.fromName(System.getProperty("cloud.logging.level", "INFO")));

            Logger.setFactory(logger.addHandler(new ConsoleLogHandler(console), new FileLogHandler()));

            System.setOut(logger.asPrintStream(LogLevel.INFO));
            System.setErr(logger.asPrintStream(LogLevel.ERROR));

            String modulePath = Arrays.stream(args).filter(s -> s.startsWith("--moduleFolder=")).findFirst().orElse("--moduleFolder=modules/").split("=")[1];

            CloudDriver<INode> driver = new NodeDriver(logger, console, Arrays.asList(args).contains("--devMode"), modulePath);

            logger.addHandler(entry -> driver.getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new DriverLogEvent(entry)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
