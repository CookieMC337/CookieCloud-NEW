package de.cookiemc.modules.sign.cloud.manager;

import de.cookiemc.common.task.Task;
import de.cookiemc.document.Bundle;
import de.cookiemc.document.DocumentFactory;
import de.cookiemc.document.IEntry;
import de.cookiemc.modules.sign.api.def.UniversalCloudSign;
import de.cookiemc.modules.sign.api.CloudSignAPI;
import de.cookiemc.modules.sign.api.ICloudSign;
import de.cookiemc.modules.sign.api.ICloudSignManager;
import de.cookiemc.modules.sign.api.protocol.SignProtocolType;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class ModuleCloudSignManager implements ICloudSignManager {

    @Setter
    private Collection<ICloudSign> allCachedCloudSigns;
    private final File signFile;

    public ModuleCloudSignManager(Path dataFolder) {
        this.allCachedCloudSigns = new ArrayList<>();
        this.signFile = dataFolder.resolve("signs.json").toFile();
    }



    @Override
    public void loadCloudSignsSync() {
        try {
            Bundle bundle = DocumentFactory.newJsonBundle(this.signFile);
            for (IEntry entry : bundle) {
                allCachedCloudSigns.add(entry.toInstance(UniversalCloudSign.class));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Task<Collection<ICloudSign>> loadCloudSignsAsync() {
        return Task.callAsync(() -> {
            Bundle bundle = DocumentFactory.newJsonBundle(this.signFile);
            for (IEntry entry : bundle) {
                allCachedCloudSigns.add(entry.toInstance(UniversalCloudSign.class));
            }
            return allCachedCloudSigns;
        });
    }


    @Override
    public Collection<ICloudSign> getAllCachedCloudSignsForTask(String taskName) {
        return this.allCachedCloudSigns.stream().filter(s -> s.getTaskName().equalsIgnoreCase(taskName)).collect(Collectors.toList());
    }

    @Override
    public Task<ICloudSign> getCloudSignAsync(UUID uniqueId) {
        return Task.callAsync(() -> this.allCachedCloudSigns.stream().filter(s -> s.getUniqueId().equals(uniqueId)).findFirst().orElse(null));
    }

    @Override
    public ICloudSign getCloudSignOrNull(UUID uniqueId) {
        return this.allCachedCloudSigns.stream().filter(s -> s.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override
    public void addCloudSign(ICloudSign sign) {
        if (this.getCloudSignOrNull(sign.getUniqueId()) != null) {
            return;
        }
        this.allCachedCloudSigns.add(sign);
        this.update();
    }

    @Override
    public void removeCloudSign(ICloudSign sign) {
        ICloudSign cloudSign = this.getCloudSignOrNull(sign.getUniqueId());
        if (cloudSign == null) {
            return;
        }
        this.allCachedCloudSigns.remove(cloudSign);
        this.update();
    }

    @Override
    public void update() {

        Bundle bundle = DocumentFactory.newJsonBundle();
        for (ICloudSign cachedCloudSign : allCachedCloudSigns) {
            bundle.add(cachedCloudSign);
        }
        try {
            bundle.saveToFile(this.signFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CloudSignAPI.getInstance()
                .performProtocolAction(
                        SignProtocolType.SYNC_CACHE,
                        buf -> buf.writeObjectCollection(
                                this.allCachedCloudSigns
                        )
                );
    }
}
