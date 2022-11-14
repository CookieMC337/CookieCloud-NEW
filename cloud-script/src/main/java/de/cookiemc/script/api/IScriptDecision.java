package de.cookiemc.script.api;

import de.cookiemc.common.function.BiSupplier;

public interface IScriptDecision {

    BiSupplier<IScript, Boolean> getChecker();

    void executeFalse(IScript script);

    void executeTrue(IScript script);

}
