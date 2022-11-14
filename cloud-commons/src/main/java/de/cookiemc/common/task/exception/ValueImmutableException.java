package de.cookiemc.common.task.exception;


import de.cookiemc.common.task.Task;

public class ValueImmutableException extends RuntimeException {

    public ValueImmutableException(Task<?> task) {
        super("Value holding " + (task.isPresent() ? "value of type " + task.get().getClass().getName() : "no value") + " is immutable!");
    }
}
