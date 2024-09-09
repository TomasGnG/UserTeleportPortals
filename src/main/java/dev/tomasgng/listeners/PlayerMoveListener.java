package dev.tomasgng.listeners;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.utils.PortalCreator;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final PortalCreator portalCreator = UserTeleportPortals.getInstance().getPortalCreator();

    @EventHandler
    public void on(PlayerMoveEvent event) {
        if(portalCreator.tempDisabledPortals.containsKey(event.getPlayer())) {
            if(hasChangedBlock(event.getFrom(), event.getTo()))
                portalCreator.tempDisabledPortals.remove(event.getPlayer());
            else
                return;
        }


        if(event.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getType() == UserTeleportPortals.getInstance().getConfigDataProvider().getPortalBlockMaterial())
            portalCreator.checkIfPlayerIsInPortal(event.getPlayer(), event.getTo());
    }

    private boolean hasChangedBlock(Location from, Location to) {
        return from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
    }
}