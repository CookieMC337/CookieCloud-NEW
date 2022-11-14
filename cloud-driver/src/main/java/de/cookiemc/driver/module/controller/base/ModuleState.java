package de.cookiemc.driver.module.controller.base;

import de.cookiemc.driver.module.ModuleController;


public enum ModuleState {

	/**
	 * @see ModuleController#loadModule()
	 */
	LOADED,

	/**
	 * @see ModuleController#enableModule()
	 */
	ENABLED,

	/**
	 * @see ModuleController#disableModule()
	 */
	DISABLED,

	/**
	 * @see ModuleController#unregisterModule()
	 */
	UNREGISTERED

}
