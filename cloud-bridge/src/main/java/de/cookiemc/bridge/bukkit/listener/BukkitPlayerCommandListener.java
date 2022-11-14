package de.cookiemc.bridge.bukkit.listener;

import de.cookiemc.bridge.IBridgePlugin;
import de.cookiemc.common.VersionInfo;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.sync.SyncedObjectType;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.SimpleDateFormat;

/**
 * This listener listens for Chat-Input on Spigot-Side
 * and checks if the input matches any registered Cloud-Sided-Command
 *
 * @author Lystx
 * @since SNAPSHOT-1.3
 */
@Data
public class BukkitPlayerCommandListener implements Listener {

    /**
     * The plugin bridge instance to handle command
     */
    private final IBridgePlugin bridge;

    @EventHandler
    public void handleChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        if (event.getMessage().equalsIgnoreCase("#CookieCloudCloud") || event.getMessage().equalsIgnoreCase("#HC")) {

            CloudDriver
                    .getInstance()
                    .getSyncedNetworkObjectAsync(SyncedObjectType.PLAYER, player.getUniqueId().toString())
                    .onTaskSucess(promise -> {

                        ICloudPlayer cloudPlayer = promise.getSyncedObjectOrNull();
                        player.sendMessage("§8§m-----------------------------");
                        player.sendMessage("§8» §7CookieCloudCloud by §bLystx §8[§e" + VersionInfo.getCurrentVersion().toString() + "§8]");
                        player.sendMessage("§8");
                        player.sendMessage("   §8» §7Proxy §8┃ §b" + cloudPlayer.getProxyServer().getName());
                        player.sendMessage("   §8» §7Server §8┃ §b" + cloudPlayer.getServer().getName());
                        player.sendMessage("   §8» §7First Login §8┃ §b" + new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss").format(cloudPlayer.getFirstLogin()));
                        player.sendMessage("§8§m-----------------------------");
                    });
            event.setCancelled(true);
        }

        //message is no command!
        if (!event.getMessage().startsWith("/")) {
            return;
        }
        bridge.handleCommandExecution(player.getUniqueId(), event.getMessage(), event::setCancelled);
    }

}
