package de.cookiemc.driver.services.template;

import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.deployment.IDeployment;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ITemplateManager {

    Collection<ITemplateStorage> getStorages();

    void registerStorage(ITemplateStorage storage);

    void deployService(@NotNull ICloudServer server, @NotNull IDeployment... deployments);

    ITemplateStorage getStorage(String name);
}
