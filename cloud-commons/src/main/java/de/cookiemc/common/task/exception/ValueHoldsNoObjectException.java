package de.cookiemc.common.task.exception;


import de.cookiemc.common.task.Task;

public class ValueHoldsNoObjectException extends RuntimeException {

    public ValueHoldsNoObjectException(Task<?> task) {
        super("Value is not holding any object!");
    }
}
