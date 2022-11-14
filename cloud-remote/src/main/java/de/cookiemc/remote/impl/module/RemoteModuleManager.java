package de.cookiemc.remote.impl.module;

import de.cookiemc.driver.module.ModuleController;
import de.cookiemc.driver.module.IModuleManager;
import de.cookiemc.driver.module.packet.RemoteModuleExecutionPacket;
import de.cookiemc.remote.Remote;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RemoteModuleManager implements IModuleManager {

    @Override
    public void resolveModules() {
        Remote.getInstance().getNetworkExecutor().sendPacket(new RemoteModuleExecutionPacket(RemoteModuleExecutionPacket.PayLoad.RESOLVE_MODULES));
    }

    @Override
    public void loadModules() {
        Remote.getInstance().getNetworkExecutor().sendPacket(new RemoteModuleExecutionPacket(RemoteModuleExecutionPacket.PayLoad.LOAD_MODULES));

    }

    @Override
    public void enableModules() {
        Remote.getInstance().getNetworkExecutor().sendPacket(new RemoteModuleExecutionPacket(RemoteModuleExecutionPacket.PayLoad.ENABLE_MODULES));
    }

    @Override
    public void disableModules() {
        Remote.getInstance().getNetworkExecutor().sendPacket(new RemoteModuleExecutionPacket(RemoteModuleExecutionPacket.PayLoad.DISABLE_MODULES));
    }

    @Override
    public void unregisterModules() {
        Remote.getInstance().getNetworkExecutor().sendPacket(new RemoteModuleExecutionPacket(RemoteModuleExecutionPacket.PayLoad.UNREGISTER_MODULES));
    }

    @NotNull
    @Override
    public Path getModulesDirectory() {
        return Paths.get("modules/");
    }

    @Override
    public void setModulesDirectory(@NotNull Path directory) {
    }

    @NotNull
    @Override
    public List<ModuleController> getModules() {
        return new ArrayList<>(Remote.getInstance().getNetworkExecutor().getPacketChannel().prepareSingleQuery()
                .execute(new RemoteModuleExecutionPacket(RemoteModuleExecutionPacket.PayLoad.RETRIEVE_MODULES))
                .syncUninterruptedly().get().buffer().readObjectCollection(RemoteModuleController.class));
    }
}
