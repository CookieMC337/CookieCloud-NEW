package de.cookiemc.common.task.exception;


import de.cookiemc.common.task.Task;

public class ValueTimedOutException extends RuntimeException {

    public ValueTimedOutException(Task<?> task) {
        super("Value has timed out!");
    }
}
