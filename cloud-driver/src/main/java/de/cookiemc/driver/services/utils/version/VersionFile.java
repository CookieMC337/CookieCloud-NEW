package de.cookiemc.driver.services.utils.version;

import de.cookiemc.driver.services.ICloudServer;

import java.io.File;
import java.io.IOException;

public abstract class VersionFile {

    public abstract void applyFile(ICloudServer ICloudServer, File file) throws IOException;

    public abstract String getFileName();
}
