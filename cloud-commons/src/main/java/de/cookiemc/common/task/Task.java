package de.cookiemc.common.task;

import de.cookiemc.common.collection.NamedThreadFactory;
import de.cookiemc.common.scheduler.Scheduler;
import de.cookiemc.common.task.def.DefaultCompletableTask;
import de.cookiemc.common.task.exception.ValueHoldsNoObjectException;
import de.cookiemc.common.task.exception.ValueImmutableException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class represents a wrapper for an {@link Object} of your choice.
 * The object inside this wrapper might be null at any time.
 *
 * <br>
 * <br> There are multiple checks to get if there is a value being held
 * like {@link Task#isNull()} or {@link Task#isPresent()}
 *
 * <br>
 * <br>Values may be immutable, so they can't be modified after they
 * have received a value or have been updated once
 * The immutable changed can be retrieved or change using {@link Task#isImmutable()}
 * and {@link Task#setImmutable(boolean)} at any time
 *
 * @author CookieMC337
 * @since SNAPSHOT-1.2
 */
public interface Task<T> extends Serializable {

    /**
     * The static executor for async calls
     */
    ExecutorService SERVICE = Executors.newCachedThreadPool(new NamedThreadFactory("WrapperTaskPool"));

    /**
     * Constructs a new empty {@link Task} with no object being held
     * The created value is not immutable, so it may be modified
     *
     * @param <T> the type of the object the new value should hold
     * @return the created value instance
     */
    static <T> Task<T> empty() {
        return newInstance((T) null);
    }

    /**
     * Constructs a new empty {@link Task} with no object being held
     * The created value is not immutable, so it may be modified
     *
     * @param <T> the type of the object the new value should hold
     * @return the created value instance
     */
    static <T> Task<T> empty(Class<T> typeClass) {
        return newInstance((T) null);
    }

    /**
     * Returns a new {@link Task} that completes when every single
     * provided {@link Task} is done
     *
     * @param tasks the tasks to run
     * @return task instance
     */
    static Task<Boolean> multiTasking(Task<?>... tasks) {

        Task<Boolean> task = empty();
        if (tasks.length == 0) {
            return task;
        }
        for (Task<?> promise : tasks) {
            promise.onTaskSucess(o -> {
                if (Arrays.stream(tasks).allMatch(Task::isPresent) && !task.isPresent()) {
                    task.setResult(true);
                }
            });
        }
        return task;
    }

    /**
     * Constructs a new pre-filled {@link Task} with an object being held
     * The created value is not immutable, so it may be modified
     *
     * @param value the value that should be held
     * @param <T>   the type of the object the new value should hold
     * @return the created value instance
     */
    static <T> Task<T> newInstance(T value) {
        return newInstance(value, false);
    }

    /**
     * Constructs a new pre-filled {@link Task} with an object being held
     * The created value is not immutable, so it may be modified
     *
     * @param value the value that should be held
     * @param <T>   the type of the object the new value should hold
     * @return the created value instance
     */
    static <T> Task<T> newInstance(Supplier<T> value) {
        return newInstance(value.get());
    }


    /**
     * Constructs a new pre-filled {@link Task} with an object being held
     * The created value will be immutable depending on the value you provide
     * as a parameter
     *
     * @param value     the value that should be held
     * @param immutable if the value may be modified
     * @param <T>       the type of the object the new value should hold
     * @return the created value instance
     */
    static <T> Task<T> newInstance(T value, boolean immutable) {
        return new DefaultCompletableTask<T>(value).setImmutable(immutable);
    }

    /**
     * Runs a {@link Task} with return-type {@link Void} delayed
     * for the given {@link TimeUnit} and value to use for this unit
     *
     * @param runnable the runnable to execute
     */
    static void runTaskLater(@NotNull Runnable runnable, TimeUnit unit, long delay) {
        Task<Void> task = Task.empty();
        Scheduler.runTimeScheduler().scheduleDelayedTask(() -> {
            runnable.run();
            task.setResult(null);
        }, unit.toMillis(delay));
    }

    /**
     * Runs a {@link Task} with return-type {@link Void} async
     *
     * @param runnable the runnable to execute
     * @return the task instance
     */
    static Task<Void> runAsync(@Nonnull Runnable runnable) {
        return callAsync(() -> {
            runnable.run();
            return null;
        });
    }

    void setAcceptSingleValue();

    /**
     * Runs a {@link Task} with return-type {@link Void} async
     *
     * @param callable the runnable to execute
     * @return the task instance
     */
    static <V> Task<V> callAsync(@Nonnull Callable<V> callable) {
        Task<V> task = empty();
        SERVICE.execute(() -> {
            try {
                task.setResult(callable.call());
            } catch (Throwable ex) {
                task.setFailure(ex);
            }
        });
        return task;
    }

    /**
     * Returns null if there is no provided value
     */
    T orNull();

    /**
     * Retrieves the current held object if it is set
     * This is unsafe to use because of Exception-Throwing
     * You should rather use {@link Task#orElse(Object)} to avoid exceptions
     * and provide your own custom value to return if nothing set
     * See why it throws {@link ValueHoldsNoObjectException} down below.
     * If there is no object being held right now and the value is immutable
     * the exception won't be thrown because there is no value, and maybe
     * it needs a value first to act immutable. After the value was changed
     * and it is tried to update another time it will of course throw the exception
     *
     * @return value or nothing
     * @throws ValueHoldsNoObjectException if there is no value held right now
     */
    T get() throws ValueHoldsNoObjectException;

    /**
     * Tries to get the current held object as a specific wrapper class
     * The method internally tries to cast the held object to the wrapper
     *
     * @param wrapperClass the class to get it as
     * @param <V>          the generic of the value to hold
     * @return value or null
     * @throws ValueHoldsNoObjectException if there is no value held right now
     */
    <V> V as(Class<? extends V> wrapperClass) throws ValueHoldsNoObjectException;

    /**
     * Provides an exception for this value
     *
     * @param ex the exception to set
     */
    Task<T> setFailure(Throwable ex);

    /**
     * Retrieves the {@link Throwable} that has been set
     * via {@link Task#setFailure(Throwable)}
     *
     * @return error (if set) or null
     */
    Throwable error();

    /**
     * Blocks the current thread and awaits for an update of this value
     *
     * @return current value
     */
    Task<T> syncUninterruptedly();

    /**
     * Sets the time-out for {@link Task#syncUninterruptedly()}
     *
     * @param unit          the unit of the timeOut
     * @param timeOut       the value for the given unit
     * @param fallbackValue the value to fall back to if timed out
     * @return current value instance
     */
    Task<T> timeOut(TimeUnit unit, int timeOut, T fallbackValue);

    /**
     * Sets the timeOut for when executing {@link #syncUninterruptedly()}
     *
     * @param timeOut the timeOut in milliseconds
     * @return current task instance
     */
    default Task<T> timeOut(int timeOut) {
        return timeOut(TimeUnit.MILLISECONDS, timeOut, null);
    }

    /**
     * Sets the timeOut for when executing {@link #syncUninterruptedly()}
     * @param unit the unit for timeOut
     * @param timeOut the timeOut value
     * @return current task instance
     */
    default Task<T> timeOut(TimeUnit unit, int timeOut) {
        return timeOut(unit, timeOut, null);
    }

    /**
     * Tries to retrieve the current held object if it is set
     * If there is no current object held, the provided value
     * will be returned.
     * Unlike {@link Task#get()} this method does not throw exceptions
     *
     * @param value the optional value if no value is held
     * @return held value or provided parameter
     */
    T orElse(T value);

    /**
     * Return the value if present, otherwise invoke the parameter supplier
     * and return the result of that invocation.
     *
     * @param other the supplier to retrieve the result if nothing is held
     * @return the current held object or the value of the supplier
     */
    T orGet(Supplier<? extends T> other);

    /**
     * Tries to retrieve the current held object.
     * If there is no current object the provided {@link Throwable} will be thrown
     *
     * @param exception the exception to throw if no value is held
     * @param <E>       the type of the exception to throw
     * @return value if held or nothing (because of interrupt by exception)
     */
    <E extends Throwable> T orThrow(Supplier<? extends E> exception);

    /**
     * Tries to retrieve the current held object.
     * If there is no current object the provided {@link Throwable} will be thrown
     *
     * @param exception the exception to throw if no value is held
     * @param <E>       the type of the exception to throw
     * @return value if held or nothing (because of interrupt by exception)
     */
    <E extends Throwable> T orThrow(E exception);

    /**
     * Performs an action after testing the provided {@link Predicate}
     * If the predicate returns false the "ifFalse" {@link Runnable} will be
     * executed and if the predicate returns true the consumer "or" will be invoked
     *
     * @param predicate the check-function
     * @param ifFalse   the task to perform if the check returns false
     * @param or        the task to perform if the check returns true
     */
    void orElseDo(Predicate<T> predicate, Runnable ifFalse, Consumer<T> or);

    /**
     * Handles the <b>ifPresent</b> {@link Consumer} if a value is currently
     * present, otherwise it will invoke the <b>orElse</b> {@link Runnable}
     *
     * @param ifPresent the handler if value is present
     * @param orElse the runnable if no value is present
     */
    void ifPresentOrElse(Consumer<T> ifPresent, Runnable orElse);

    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return a {@link Task} describing the
     * result. Otherwise, return an empty {@link Task}
     *
     * @param <V>    The type of the result of the mapping function
     * @param mapper a mapping function to apply to the value, if present
     * @return a new value describing the result of applying a mapping
     */
    <V> Task<V> map(Function<T, V> mapper);

    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return a {@link Task} describing the
     * result. Otherwise, supplies the ifNull parameter
     *
     * @param ifNull the supplier if value is null
     * @param <E> The type of the result of the mapping function
     * @param mapper a mapping function to apply to the value, if present
     * @return a new value describing the result of applying a mapping
     */
    <E> E mapOrElse(Function<T, E> mapper, Supplier<E> ifNull);

    /**
     * If a value is present, and the value matches the given predicate,
     * return a {@link Task} containing the value, otherwise returns an empty one
     *
     * @param predicate a predicate to apply to the value, if present
     * @return a new value containing the value of this current {@link Task}
     */
    Task<T> filter(Predicate<? super T> predicate);

    /**
     * Updates the current object of this value reference
     *
     * @param value the value to set
     * @return current value
     */
    Task<T> setResult(T value) throws ValueImmutableException;

    /**
     * Sets the immutable state of this value
     *
     * @param immutable the state if it may be modified
     */
    Task<T> setImmutable(boolean immutable);

    /**
     * Checks if this value is immutable
     * (So if it may be modified)
     */
    boolean isImmutable();

    /**
     * Checks if this instance holds an object currently
     */
    boolean isPresent();

    /**
     * Checks if this operation was done
     */
    boolean isDone();

    /**
     * If this operation was successful
     */
    boolean isSuccess();

    /**
     * Checks if this instance does not hold an object currently
     */
    boolean isNull();

    /**
     * Performs the provided action if an object is currently held
     *
     * @param value the handler to perform
     */
    void ifPresent(Consumer<T> value);

    /**
     * Waits until a value is provided in this value
     *
     * @param value the handler to perform
     */
    void whenPresent(WrapperListener<T> value);

    /**
     * Adds an update listener as {@link Consumer} to this value
     * that handles this whole value instance when updating
     *
     * @param listener the listener as consumer to add
     * @return current value instance
     */
    Task<T> registerListener(Consumer<Task<T>> listener);

    /**
     * Calls the handler when an exception is caught in this task
     *
     * @param e the handler to handle the error
     * @return current task instance
     */
    Task<T> onTaskFailed(Consumer<Throwable> e);

    /**
     * Adds a simple updating listener as {@link Consumer} to this value
     *
     * @param listener the listener as consumer to add
     * @return current value instance
     */
    Task<T> onTaskSucess(Consumer<T> listener);

    /**
     * Waits until a value is provided in this value
     *
     * @param value      the handler to perform
     * @param checkSleep the time to sleep before checking again
     */
    void whenPresent(int checkSleep, WrapperListener<T> value);

    /**
     * Performs the provided action if no object is currently being held
     *
     * @param consumer the action to perform
     */
    void ifEmpty(Consumer<Task<T>> consumer);

}
