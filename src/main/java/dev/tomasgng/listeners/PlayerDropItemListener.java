package dev.tomasgng.listeners;
import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.utils.PortalCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {

    private final Material supportedItem = UserTeleportPortals.getInstance().getConfigDataProvider().getPortalItemMaterial();
    private final Material supportedBlock = UserTeleportPortals.getInstance().getConfigDataProvider().getPortalBlockMaterial();

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        PortalCreator portalCreator = UserTeleportPortals.getInstance().getPortalCreator();

        Bukkit.getScheduler().runTaskLater(UserTeleportPortals.getInstance(), () -> {
            Item item = event.getItemDrop();

            if(portalCreator.isPortalItem(item.getItemStack()))
                return;

            if(item.getItemStack().getAmount() > 1)
                return;

            Block block = item.getLocation().getWorld().getBlockAt(item.getLocation().subtract(0, 1, 0));

            if(item.getItemStack().getType() != supportedItem)
                return;

            if(item.getLocation().getBlock().getFace(block) != BlockFace.DOWN)
                return;

            if(block.getBlockData().getMaterial() != supportedBlock)
                return;

            portalCreator.createPortalItem(event);
        }, 15L);
    }

}