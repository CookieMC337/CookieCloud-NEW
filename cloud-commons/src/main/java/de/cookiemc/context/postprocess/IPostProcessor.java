package de.cookiemc.context.postprocess;

import de.cookiemc.context.IApplicationContext;

public interface IPostProcessor {

    Object postProcessorBeforeInitialisation(String name, Object value, IApplicationContext context);
    Object postProcessorAfterInitialisation(String name, Object value, IApplicationContext context);
}
