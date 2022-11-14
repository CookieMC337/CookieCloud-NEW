package de.cookiemc.driver.module.controller;

import de.cookiemc.http.api.HttpServer;
import de.cookiemc.driver.module.IModule;
import de.cookiemc.driver.module.ModuleController;
import lombok.Getter;
import lombok.Setter;

@Getter
public class AbstractModule implements IModule {

	/**
	 * The controller for this module (is being set when being loaded)
	 */
	@Setter
	protected ModuleController controller;

	/**
	 * The instance that is being set from Node loader
	 */
	@Setter
	protected HttpServer httpServer;

	public void registerTasks(Object moduleTasksClassInstance) {
		controller.registerModuleTasks(moduleTasksClassInstance);
	}

	@Override
	public String toString() {
		return getController().getModuleConfig().getFullName() + " (" + getController().getJarFile().getFileName() + ")";
	}

}
