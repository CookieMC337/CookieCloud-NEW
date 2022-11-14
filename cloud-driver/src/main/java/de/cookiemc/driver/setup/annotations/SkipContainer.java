package de.cookiemc.driver.setup.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SkipContainer {

    SkipQuestion[] value();
}
