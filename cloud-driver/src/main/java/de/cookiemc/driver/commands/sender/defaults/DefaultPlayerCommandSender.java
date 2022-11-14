package de.cookiemc.driver.commands.sender.defaults;

import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.commands.sender.PlayerCommandSender;
import de.cookiemc.driver.player.executor.PlayerExecutor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.UUID;


@AllArgsConstructor @Getter
public class DefaultPlayerCommandSender implements PlayerCommandSender {

	private final ICloudPlayer player;

	@Override
	public void sendMessage(@Nonnull String message) {
		PlayerExecutor.forPlayer(player).sendMessage(message);
	}


	@Override
	public boolean hasPermission(@Nonnull String permission) {
		return player.hasPermission(permission);
	}

	@Nonnull
	@Override
	public String getName() {
		return player.getName();
	}

	@Nonnull
	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public String toString() {
		return "PlayerCommandSender[name=" + getName() + " uuid=" + getUniqueId() + "]";
	}

}
