package de.cookiemc.node.handler.packet.normal;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.module.ModuleController;
import de.cookiemc.driver.module.IModuleManager;
import de.cookiemc.driver.module.controller.base.ModuleCopyType;
import de.cookiemc.driver.module.packet.RemoteModuleExecutionPacket;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.networking.protocol.wrapped.ChanneledPacketAction;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class NodeModulePacketHandler implements PacketHandler<RemoteModuleExecutionPacket> {

    @Override
    public void handle(PacketChannel wrapper, RemoteModuleExecutionPacket packet) {
        PacketBuffer buffer = packet.buffer();
        RemoteModuleExecutionPacket.PayLoad payLoad = buffer.readEnum(RemoteModuleExecutionPacket.PayLoad.class);

        switch (payLoad) {
            case LOAD_MODULES:
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(IModuleManager.class).loadModules();
                break;
            case ENABLE_MODULES:
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(IModuleManager.class).enableModules();
                break;
            case DISABLE_MODULES:
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(IModuleManager.class).disableModules();
                break;
            case UNREGISTER_MODULES:
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(IModuleManager.class).unregisterModules();
                break;
            case RESOLVE_MODULES:
                CloudDriver.getInstance().getProviderRegistry().getUnchecked(IModuleManager.class).resolveModules();
                break;
            case RETRIEVE_MODULES:
                wrapper.prepareResponse().buffer(buf -> buf.writeObjectCollection(CloudDriver.getInstance().getProviderRegistry().getUnchecked(IModuleManager.class).getModules())).execute(packet);
                break;
            case TRANSFER_MODULES:
                ChanneledPacketAction<Void> response = wrapper.prepareResponse();
                List<ModuleController> copyModules = CloudDriver.getInstance().getProviderRegistry().getUnchecked(IModuleManager.class).getModules().stream().filter(m -> m.getModuleConfig().getCopyType() != ModuleCopyType.NONE).collect(Collectors.toList());
                response.buffer(buf -> buf.writeInt(copyModules.size()));
                for (ModuleController module : copyModules) {
                    response.buffer(buf -> {
                        try {
                            buf.writeString(module.getJarFile().toFile().getName());
                            buf.writeString(module.getDataFolder().toFile().getName());

                            buf.writeFile(module.getJarFile().toFile());
                            buf.writeFile(module.getDataFolder().toFile());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                response.execute(packet);
                break;
        }
    }
}
