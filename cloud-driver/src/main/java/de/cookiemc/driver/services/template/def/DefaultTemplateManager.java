package de.cookiemc.driver.services.template.def;

import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.deployment.IDeployment;
import de.cookiemc.driver.services.template.ITemplate;
import de.cookiemc.driver.services.template.ITemplateManager;
import de.cookiemc.driver.services.template.ITemplateStorage;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class DefaultTemplateManager implements ITemplateManager {

    /**
     * All cached storages
     */
    private final Collection<ITemplateStorage> storages = new ArrayList <>();

    @Override
    public void registerStorage(ITemplateStorage storage) {
        if (this.getStorage(storage.getName()) == null) {
            this.storages.add(storage);
        }
    }

    @Override
    public void deployService(@NotNull ICloudServer server, @NotNull IDeployment... deployments) {
        for (IDeployment deployment : deployments) {
            ITemplate template = deployment.getTemplate();
            ITemplateStorage storage = template.getStorage();
            if (storage != null) {
                storage.deployService(server, deployment);
            }
        }
    }

    @Override
    public ITemplateStorage getStorage(String name) {
        return this.storages.stream().filter(ts -> ts.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
