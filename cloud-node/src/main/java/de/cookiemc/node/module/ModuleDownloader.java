package de.cookiemc.node.module;

import de.cookiemc.common.DriverUtility;
import de.cookiemc.common.VersionInfo;
import de.cookiemc.common.logging.ConsoleColor;
import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.misc.FileUtils;
import de.cookiemc.common.progressbar.ProgressBar;
import de.cookiemc.common.progressbar.ProgressBarStyle;
import de.cookiemc.common.task.Task;
import de.cookiemc.document.Document;
import de.cookiemc.document.DocumentFactory;
import de.cookiemc.document.IEntry;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.console.Console;
import de.cookiemc.driver.module.IModuleManager;
import de.cookiemc.node.NodeDriver;
import de.cookiemc.node.module.updater.ModuleInfo;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleDownloader {

    private final String applicationFileURL = "https://raw.githubusercontent.com/CookieMC337/CookieCloud/master/application.json";
    private Collection<ModuleInfo> cachedModules;


    @SneakyThrows
    public Collection<ModuleInfo> loadProvidedModules() {
        if (cachedModules != null) {
            return cachedModules;
        }
        Collection<ModuleInfo> modules = new ArrayList<>();

        Document document = Document.newJsonDocumentByURL(applicationFileURL);
        for (IEntry entry : document.getBundle("modules")) {
            Document doc = entry.toDocument();
            if (!doc.has("name") || !doc.has("url") || !doc.has("version")) {
                Logger.constantInstance().error("Couldn't find attributes for ModuleInfo 'name' or 'url' or 'version' in following document:");
                Logger.constantInstance().error(doc.asRawJsonString());
                continue;
            }
            ModuleInfo moduleInfo = new ModuleInfo(
                    doc.get("name").toString(),
                    doc.get("url").toString(),
                    VersionInfo.fromString(doc.get("version").toString())
            );
            Logger.constantInstance().debug("Loaded ModuleInfo[name={}, url={}, version={}]", moduleInfo.getName(), moduleInfo.getUrl(), moduleInfo.getVersion());
            modules.add(moduleInfo);
        }
        return (cachedModules = modules);
    }

    public String getModuleUrl(ModuleInfo info) {
        VersionInfo currentVersion = info.getVersion();
        String url = info.getUrl();

        //replace url place holders
        url = url.replace("{cloud.baseUrl}", NodeDriver.getInstance().getBaseUrl());
        url = url.replace("{module.name}", info.getName());
        url = url.replace("{module.version}", currentVersion.toString());

        return url;
    }

    public Task<ModuleInfo> updateModule(ModuleInfo module) {
        Task<ModuleInfo> task = Task.empty();
        return Task.callAsync(() -> {

            String url = module.getUrl();
            String name = module.getName();
            VersionInfo currentVersion = module.getVersion();

            //replace url place holders
            url = url.replace("{cloud.baseUrl}", NodeDriver.getInstance().getBaseUrl());
            url = url.replace("{module.name}", name);
            url = url.replace("{module.version}", currentVersion.toString());

            ModuleInfo localModule = findCurrentModule(name, url);
            if (localModule == null || module.getVersion().isNewerAs(localModule.getVersion())) {
                Logger.constantInstance().info("Module[val={}] is either not existing or needs to be updated to Version[val={}, url={}]", module.getName(), module.getVersion(), url);
                downloadModule(module, url)
                        .onTaskSucess(e -> task.setResult(module))
                        .onTaskFailed(task::setFailure);
            } else {
                Logger.constantInstance().info("Module[name={}, ver={}] is up to date", module.getName(), module.getVersion());
            }
            return null;
        });
    }


    public Task<ModuleInfo> updateModule(String name) {

        Collection<ModuleInfo> modules = loadProvidedModules();
        ModuleInfo moduleInfo = modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        return moduleInfo == null ? Task.empty() : updateModule(moduleInfo);
    }

    public Task<Integer> updateModules() {
        Task<Integer> task = Task.empty();
        Collection<ModuleInfo> modules = loadProvidedModules();

        AtomicInteger updateCount = new AtomicInteger(0);
        for (ModuleInfo module : modules) {
            this.updateModule(module)
                    .onTaskSucess(m -> {
                        updateCount.set((updateCount.get() + 1));
                        if (updateCount.get() >= modules.size()) {
                            task.setResult(updateCount.get());
                        }
                    })
                    .onTaskFailed(task::setFailure);
        }
        return task;
    }

    public Task<Path> downloadModule(ModuleInfo module, String url) {
        Task<Path> task = Task.empty();
        ProgressBar pb = new ProgressBar(ProgressBarStyle.COLORED_UNICODE_BLOCK, 100L);

        //manage console
        Console console = NodeDriver.getInstance().getConsole();
        String prompt = console.getPrompt();
        console.setPrompt("");

        pb.setFakePercentage(50, 100);
        pb.setTaskName("??8?? ??bDownloading ??f" + module.getName());
        pb.setPrintAutomatically(true);
        pb.setExpandingAnimation(true);

        pb.setPrinter(progress -> {
            NodeDriver.getInstance().getConsole().writePlain(ConsoleColor.toColoredString('??', progress));
        });

        DriverUtility.downloadVersion(url, CloudDriver.getInstance().getProviderRegistry().getUnchecked(IModuleManager.class).getModulesDirectory().resolve(module.getName() + "-" + module.getVersion() + ".jar"), pb)
                .onTaskSucess(v -> {
                    task.setResult(v);
                    console.setPrompt(prompt);
                })
                .onTaskFailed(e -> {
                    task.setFailure(e);
                    Logger.constantInstance().error("Couldn't download Module[val={}, url={}] Error: {}", module.getName(), url, e);
                    console.setPrompt(prompt);
                });

        return task;
    }

    private ModuleInfo findCurrentModule(String name, String url) {
        Path moduleFile = FileUtils.list(CloudDriver.getInstance().getProviderRegistry().getUnchecked(IModuleManager.class).getModulesDirectory())
                .filter(path -> path.getFileName().toString().endsWith(".jar"))
                .filter(path -> path.getFileName().toString().contains(name))
                .findFirst()
                .orElse(null);
        if (moduleFile == null) {
            return null;
        }
        Document document = loadDocument(moduleFile.toFile(), "config.json");

        return document == null ? null
                : new ModuleInfo(
                name,
                url,
                VersionInfo.fromString(document.getString("version"))
        );
    }


    private String loadJson(File jarFile, String filename) {
        try {
            JarFile jf = new JarFile(jarFile);
            JarEntry je = jf.getJarEntry(filename);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(jf.getInputStream(je)))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                jf.close();
                br.close();
                return builder.toString();
            } catch (Exception e) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private Document loadDocument(File jarFile, String filename) {
        String jsonInput = this.loadJson(jarFile, filename);
        if (jsonInput == null) {
            return null;
        }
        return DocumentFactory.newJsonDocument(jsonInput);
    }


}
