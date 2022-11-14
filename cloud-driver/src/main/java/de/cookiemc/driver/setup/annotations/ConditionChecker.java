package de.cookiemc.driver.setup.annotations;

import de.cookiemc.common.function.BiSupplier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConditionChecker {

    Class<? extends BiSupplier<String, Boolean>> value();

    String message();
}
