package de.cookiemc.driver.exception;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.DriverEnvironment;

public class IncompatibleDriverEnvironment extends CloudException {

    public IncompatibleDriverEnvironment(DriverEnvironment rightEnvironment) {
        super("This action can't be performed on Environment " + CloudDriver.getInstance().getEnvironment() + " but on " + rightEnvironment + ".");
    }

    public static void throwIfNeeded(DriverEnvironment rightEnvironment) {
        if (CloudDriver.getInstance().getEnvironment() != rightEnvironment) {
            throw new IncompatibleDriverEnvironment(rightEnvironment);
        }
    }
}
