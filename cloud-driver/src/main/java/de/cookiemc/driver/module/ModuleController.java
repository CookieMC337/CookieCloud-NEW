package de.cookiemc.driver.module;

import de.cookiemc.document.wrapped.StorableDocument;
import de.cookiemc.driver.module.controller.base.ModuleConfig;
import de.cookiemc.driver.module.controller.base.ModuleState;
import de.cookiemc.driver.module.controller.ModuleClassLoader;
import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;

import javax.annotation.Nonnull;
import java.nio.file.Path;


public interface ModuleController extends IBufferObject {

	boolean isEnabled();

	boolean isEnabled(boolean defaultValue);

	boolean isInitialized();

	@Nonnull
    IModule getModule();

	void loadModule();

	void enableModule();

	void disableModule();

	void unregisterModule();

	@Nonnull
    IModuleManager getManager();

	@Nonnull
	Path getJarFile();

	@Nonnull
	Path getDataFolder();

	@Nonnull
	StorableDocument getConfig();

	@Nonnull
	StorableDocument reloadConfig();

	@Nonnull
    ModuleState getState();

	@Nonnull
    ModuleConfig getModuleConfig();

	@Nonnull
    ModuleClassLoader getClassLoader();

	void registerModuleTasks(Object classHolder);

	void initConfig() throws Exception;

	void initModule() throws Exception;
}
