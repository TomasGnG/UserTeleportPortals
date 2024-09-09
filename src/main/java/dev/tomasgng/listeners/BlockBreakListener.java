package dev.tomasgng.listeners;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.config.dataprovider.ConfigDataProvider;
import dev.tomasgng.utils.LocationUtils;
import dev.tomasgng.utils.PortalCreator;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final ConfigDataProvider config = UserTeleportPortals.getInstance().getConfigDataProvider();
    private final PortalCreator portalCreator = UserTeleportPortals.getInstance().getPortalCreator();

    @EventHandler
    public void on(BlockBreakEvent event) {
        Material material = event.getBlock().getType();

        if(material != config.getPortalBlockMaterial())
            return;

        portalCreator.removePortal(LocationUtils.toCenterLocation(event.getBlock().getLocation()).add(0, 1, 0));
    }

}