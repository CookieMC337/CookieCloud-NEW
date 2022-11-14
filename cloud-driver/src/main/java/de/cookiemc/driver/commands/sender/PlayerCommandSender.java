package de.cookiemc.driver.commands.sender;

import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.common.CloudMessages;
import de.cookiemc.driver.player.executor.PlayerExecutor;

import javax.annotation.Nonnull;
import java.util.UUID;


public interface PlayerCommandSender extends CommandSender {

	@Nonnull
	UUID getUniqueId();

	@Nonnull
    ICloudPlayer getPlayer();


	/**
	 * Puts the cloud-prefix defined in {@link CloudMessages}
	 * in front of the message.<br>
	 * To send a normal message use {@link PlayerExecutor#sendMessage(String)}
	 *
	 * @param message the message to send
	 */
	@Override
	void sendMessage(String message);
}
