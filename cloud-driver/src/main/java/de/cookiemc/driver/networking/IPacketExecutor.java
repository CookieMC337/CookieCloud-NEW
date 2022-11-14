package de.cookiemc.driver.networking;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.networking.protocol.packets.IPacket;

// TODO: 21.08.2022 documentation
public interface IPacketExecutor {

    void sendPacket(IPacket packet);

    Task<Void> sendPacketAsync(IPacket packet);

}
