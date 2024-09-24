package dev.tomasgng.listeners;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.config.dataprovider.ConfigDataProvider;
import dev.tomasgng.config.dataprovider.MessageDataProvider;
import dev.tomasgng.utils.LocationUtils;
import dev.tomasgng.utils.PortalCreator;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    private final PortalCreator portalCreator = UserTeleportPortals.getInstance().getPortalCreator();
    private final MessageDataProvider messageDataProvider = UserTeleportPortals.getInstance().getMessageDataProvider();
    private final ConfigDataProvider configDataProvider = UserTeleportPortals.getInstance().getConfigDataProvider();
    private final BukkitAudiences adventure = UserTeleportPortals.getInstance().getAdventure();

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Material supportedBlock = configDataProvider.getPortalBlockMaterial();

        if(event.getHand() != EquipmentSlot.HAND)
            return;

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(event.getItem() == null)
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if(!portalCreator.isPortalItem(item))
            return;

        event.setCancelled(true);
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        if(!portalCreator.canCreatePortal(player)) {
            adventure.player(player).sendMessage(messageDataProvider.getPortalLimitReached());
            return;
        }

        Location location = LocationUtils.toCenterLocation(event.getClickedBlock().getLocation());

        if(portalCreator.isLocationAlreadyUsed(location.add(0, 1, 0))) {
            adventure.player(player).sendMessage(messageDataProvider.getSourceLocationAlreadyBeingUsed());
            return;
        }

        if(event.getClickedBlock().getType() != supportedBlock) {
            portalCreator.updatePortalItemDestination(item, location.clone().subtract(0, 0.5, 0));
            adventure.player(player).sendMessage(messageDataProvider.getPortalDestinationChanged());
            return;
        }

        if(location.getWorld().getName().equalsIgnoreCase(portalCreator.getDestination(item).getWorld().getName())) {
            int currentDistance = (int) location.distanceSquared(portalCreator.getDestination(item));
            int maxPortalDistance = portalCreator.getMaxPortalDistance(player);

            if(!player.isOp() && currentDistance > (maxPortalDistance * maxPortalDistance)) {
                adventure.player(player).sendMessage(messageDataProvider.getMaxPortalDistanceReached(maxPortalDistance));
                return;
            }

            if(location.distance(portalCreator.getDestination(item)) < configDataProvider.getPortalMinimumDistance()) {
                adventure.player(player).sendMessage(messageDataProvider.getSourceLocationTooCloseToDestination());
                return;
            }
        }

        portalCreator.createPortal(event.getPlayer().getName(), item, location);
        adventure.player(player).sendMessage(messageDataProvider.getPortalCreated());
        player.getInventory().remove(item);
    }
}