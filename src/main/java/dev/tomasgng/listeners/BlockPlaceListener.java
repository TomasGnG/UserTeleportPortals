package dev.tomasgng.listeners;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.utils.LocationUtils;
import dev.tomasgng.utils.PortalCreator;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final PortalCreator portalCreator = UserTeleportPortals.getInstance().getPortalCreator();

    @EventHandler
    public void on(BlockPlaceEvent event) {
        Location location = LocationUtils.toCenterLocation(event.getBlockPlaced().getLocation());

        if(!portalCreator.isLocationAlreadyUsed(location) && !portalCreator.isLocationAlreadyUsed(location.clone().subtract(0, 1, 0)))
            return;

        if(event.getBlockPlaced().isPassable())
            return;

        String materialName = event.getBlockPlaced().getType().toString();

        if(!materialName.contains("DOOR") && !materialName.contains("CARPET"))
            event.setCancelled(true);
    }

}