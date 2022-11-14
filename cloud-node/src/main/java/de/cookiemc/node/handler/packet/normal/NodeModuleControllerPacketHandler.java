package de.cookiemc.node.handler.packet.normal;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.module.ModuleController;
import de.cookiemc.driver.module.IModuleManager;
import de.cookiemc.driver.module.controller.base.ModuleConfig;
import de.cookiemc.driver.module.packet.RemoteModuleControllerPacket;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;

public class NodeModuleControllerPacketHandler implements PacketHandler<RemoteModuleControllerPacket> {

    @Override
    public void handle(PacketChannel wrapper, RemoteModuleControllerPacket packet) {

        PacketBuffer buffer = packet.buffer();
        RemoteModuleControllerPacket.PayLoad payLoad = buffer.readEnum(RemoteModuleControllerPacket.PayLoad.class);
        ModuleConfig moduleConfig = buffer.readObject(ModuleConfig.class);
        IModuleManager moduleManager = CloudDriver.getInstance().getProviderRegistry().getUnchecked(IModuleManager.class);
        ModuleController controller = moduleManager.getModules().stream().filter(mc -> mc.getModuleConfig().getName().equalsIgnoreCase(moduleConfig.getName())).findFirst().orElse(null);
        if (controller == null) {
            return;
        }

        switch (payLoad) {
            case INIT_CONFIG:
                try {
                    controller.initConfig();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case INIT_MODULE:
                try {
                    controller.initModule();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case LOAD_MODULE:
                controller.loadModule();
                break;
            case ENABLE_MODULE:
                controller.enableModule();
                break;
            case DISABLE_MODULE:
                controller.disableModule();
                break;
            case UNREGISTER_MODULE:
                controller.unregisterModule();
                break;
            case RELOAD_CONFIG:
                wrapper.prepareResponse().buffer(buf -> buf.writeDocument(controller.reloadConfig())).execute(packet);
                break;
            case GET_JAR_FILE:
                wrapper.prepareResponse().buffer(buf -> buf.writeString(controller.getJarFile().toString())).execute(packet);
                break;
            case GET_DATA_FOLDER:
                wrapper.prepareResponse().buffer(buf -> buf.writeString(controller.getDataFolder().toString())).execute(packet);
                break;
        }
    }

}
