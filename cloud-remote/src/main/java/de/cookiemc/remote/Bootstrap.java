package de.cookiemc.remote;

import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.logging.handler.HandledAsyncLogger;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.driver.DriverLogEvent;
import de.cookiemc.driver.services.utils.RemoteIdentity;
import de.cookiemc.remote.impl.log.DefaultLogHandler;
import lombok.var;

import java.io.File;
import java.lang.instrument.Instrumentation;

public class Bootstrap {


    private static Instrumentation instrumentationInstance;

    public static void premain(String premainArgs, Instrumentation instrumentation) {
        instrumentationInstance = instrumentation;
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        instrumentationInstance = instrumentation;
    }


    public static void main(String[] args) {
        var identity = RemoteIdentity.read(new File("property.json"));
        var logger = new HandledAsyncLogger(identity.getLogLevel());
        logger.addHandler(new DefaultLogHandler());
        logger.addHandler(entry -> CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new DriverLogEvent(entry)));
        Logger.setFactory(logger);


        var remote = new Remote(identity, logger, instrumentationInstance, args);

        try {
            remote.startApplication();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
