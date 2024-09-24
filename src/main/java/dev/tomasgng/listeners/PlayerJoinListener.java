package dev.tomasgng.listeners;

import dev.tomasgng.UserTeleportPortals;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final MiniMessage mm = MiniMessage.miniMessage();
    BukkitAudiences adventure = UserTeleportPortals.getInstance().getAdventure();

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if(!event.getPlayer().isOp())
            return;

        Audience audience = adventure.player(event.getPlayer());

        if(!UserTeleportPortals.getInstance().getVersionChecker().isLatestVersion()) {
            audience.sendMessage(mm.deserialize("<yellow>UserTeleportPortals <dark_gray>| <yellow>There is a new update update available."));
            audience.sendMessage(mm.deserialize("<yellow>UserTeleportPortals <dark_gray>| <yellow>Update the plugin by using /utp update"));
        }
    }

}