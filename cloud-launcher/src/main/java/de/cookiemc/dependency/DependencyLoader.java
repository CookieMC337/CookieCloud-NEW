package de.cookiemc.dependency;

import de.cookiemc.IdentifiableClassLoader;
import de.cookiemc.Launcher;
import de.cookiemc.common.DriverUtility;
import de.cookiemc.context.annotations.ApplicationParticipant;
import de.cookiemc.context.annotations.CacheContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

@ApplicationParticipant
public class DependencyLoader {


    @CacheContext
    private Launcher launcher;


    public Collection<URL> loadDependencyURLs() throws IOException {
        Collection<URL> dependencyResources = new ArrayList<>();

        for (Dependency dependency : this.launcher.getDependencies()) {
            if (this.launcher.getRepositories().containsKey(dependency.getRepository())) {
                Path path = Launcher.LAUNCHER_LIBS.resolve(dependency.toPath());

                this.installLibrary(this.launcher.getRepositories().get(dependency.getRepository()).getUrl(), dependency, path);

                dependencyResources.add(path.toUri().toURL());
                launcher.getLogger().info("Loaded Dependency[group={}, artifact={}, version={}, repo={}]", dependency.getGroup(), dependency.getName(), dependency.getVersion(), dependency.getRepository());
                ClassLoader ccl = Thread.currentThread().getContextClassLoader();
                if (ccl instanceof IdentifiableClassLoader) {
                    IdentifiableClassLoader custom = (IdentifiableClassLoader)ccl;
                    custom.addURL(path.toUri().toURL());
                }
            } else {
                throw new IllegalArgumentException("Dependency " + dependency + " does not match any registered Repository!");
            }
        }

        return dependencyResources;
    }

    private void installLibrary(String repositoryURL, Dependency dependency, Path path) throws IOException {
        if (!Files.exists(path)) {

            Files.createDirectories(path.getParent());

            String dependencyName = dependency.getGroup() + ":" + dependency.getName() + ":"
                    + dependency.getVersion() + ""
                    + ".jar";

            launcher.getLogger().info("Installing dependency {} from repository {}...", dependencyName, dependency.getRepository());

            try (InputStream inputStream = DriverUtility.readInputStreamFromURL(repositoryURL + "/" + dependency.toPath().toString().replace(File.separatorChar, '/'))) {
                Files.copy(inputStream, path);
            }

        }
    }
}