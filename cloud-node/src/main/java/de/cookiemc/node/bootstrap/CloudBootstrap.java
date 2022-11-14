package de.cookiemc.node.bootstrap;

import de.cookiemc.IdentifiableClassLoader;
import de.cookiemc.common.function.ExceptionallyConsumer;
import de.cookiemc.common.misc.FileUtils;
import de.cookiemc.node.bootstrap.library.DependencyLoader;
import de.cookiemc.node.bootstrap.script.IncludeDependencyCommand;
import de.cookiemc.node.bootstrap.script.IncludeRepositoryCommand;
import de.cookiemc.node.bootstrap.library.Dependency;
import de.cookiemc.node.bootstrap.library.Repository;
import de.cookiemc.script.api.IScript;
import de.cookiemc.script.api.IScriptLoader;
import de.cookiemc.script.api.impl.DefaultScriptLoader;
import de.cookiemc.script.defaults.DefaultModifyCommand;
import de.cookiemc.script.defaults.DefaultPrintCommand;
import de.cookiemc.script.defaults.DefaultRunCommand;
import de.cookiemc.script.defaults.DefaultVarCommand;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CloudBootstrap {

    private static URL getCurrentURL() {

        String s = CloudBootstrap.class.getName();
        int i = s.lastIndexOf(".");
        s = s.substring(i + 1);
        s = s + ".class";
        return CloudBootstrap.class.getResource(s);
    }

    public static void main(String[] args) throws URISyntaxException, MalformedURLException {

        Map<String, Repository> repositories = new HashMap<>();
        List<Dependency> includedDependencies = new ArrayList<>();

        IScriptLoader loader = new DefaultScriptLoader();

        loader.registerCommand(new DefaultPrintCommand());
        loader.registerCommand(new DefaultRunCommand());
        loader.registerCommand(new DefaultVarCommand());
        loader.registerCommand(new DefaultModifyCommand());
        loader.registerCommand(new IncludeDependencyCommand(includedDependencies::add));
        loader.registerCommand(new IncludeRepositoryCommand(r -> repositories.put(r.getName(), r)));

        Path launcherFile = Paths.get("node.hc");
        if (!Files.exists(launcherFile)) {
            try {
                FileUtils.copy(
                        ClassLoader.getSystemResourceAsStream("node.hc"),
                        Files.newOutputStream(launcherFile)
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        IScript script = loader.loadScript(launcherFile);
        if (script == null) {
            System.out.println("Couldn't load script!");
            return;
        }
        script.executeAsync()
                .onTaskSucess((ExceptionallyConsumer<Void>) v -> {

                    DependencyLoader dependencyLoader = new DependencyLoader(repositories, includedDependencies);

                    Collection<URL> dependencyResources;
                    try {
                        dependencyResources = dependencyLoader.loadDependencyURLs();
                        dependencyResources.add(getCurrentURL()); //adding current jar file
                    } catch (IOException exception) {
                        throw new RuntimeException("Unable to install needed dependencies!", exception);
                    }


                    IdentifiableClassLoader classLoader = new IdentifiableClassLoader(dependencyResources.toArray(new URL[0]));

                    Thread thread = new Thread(() -> {
                        try {
                            Method method = classLoader.loadClass(InternalBootstrap.class.getName()).getMethod("main", String[].class);
                            method.invoke(null, (Object) args);
                        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException exception) {
                            exception.printStackTrace();
                        }
                    });

                    try {
                        Thread.currentThread().setContextClassLoader(classLoader);
                        Field scl = ClassLoader.class.getDeclaredField("scl"); // Get system class loader
                        scl.setAccessible(true); // Set accessible
                        scl.set(null, classLoader); // Update it to your class loader
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    thread.setPriority(Thread.MIN_PRIORITY);
                    thread.setContextClassLoader(classLoader);
                    thread.start();


                });


    }

}
