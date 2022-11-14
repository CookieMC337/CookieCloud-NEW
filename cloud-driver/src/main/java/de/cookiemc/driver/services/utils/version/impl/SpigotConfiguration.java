package de.cookiemc.driver.services.utils.version.impl;

import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.utils.version.VersionFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SpigotConfiguration extends VersionFile {

    @Override
    public void applyFile(ICloudServer ICloudServer, File file) throws IOException {

        URL resource = getClass().getResource("/impl/files/spigot.yml");
        if (resource != null) {
            FileUtils.copyURLToFile(resource, file);
        }
    }

    @Override
    public String getFileName() {
        return "spigot.yml";
    }
}
