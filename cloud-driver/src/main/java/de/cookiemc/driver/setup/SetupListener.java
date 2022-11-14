package de.cookiemc.driver.setup;

import de.cookiemc.common.function.ExceptionallyBiConsumer;

public interface SetupListener<T extends Setup<?>> extends ExceptionallyBiConsumer<T, SetupControlState> {


}
