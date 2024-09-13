package dev.tomasgng.utils;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.config.dataprovider.ConfigDataProvider;
import dev.tomasgng.database.DatabaseManager;
import dev.tomasgng.database.Portal;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class PortalCreator {

    private final ConfigDataProvider configDataProvider = UserTeleportPortals.getInstance().getConfigDataProvider();
    private final DatabaseManager db = UserTeleportPortals.getInstance().getDatabaseManager();
    private final Map<UUID, Location> tempPortals = new HashMap<>();
    private final NamespacedKey portalItemKey = new NamespacedKey(UserTeleportPortals.getInstance(), "portalCreationItem");
    private final List<Portal> dbPortals = new ArrayList<>();

    public final Map<Player, Location> tempDisabledPortals = new HashMap<>();

    public PortalCreator() {
        updateCreatedPortalsVariable();
        startParticleSpawner();
    }

    private void updateCreatedPortalsVariable() {
        dbPortals.clear();
        dbPortals.addAll(db.getPortals());
    }

    private ItemStack createBaseItem() {
        ItemStack item = new ItemStack(configDataProvider.getPortalItemMaterial());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(configDataProvider.getPortalItemDisplayname()));
        meta.addEnchant(Enchantment.FORTUNE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    private void startParticleSpawner() {
        Random random = new Random();
        Bukkit.getScheduler().runTaskTimer(UserTeleportPortals.getInstance(), scheduledTask -> {
            for (Portal portal : dbPortals) {
                double randomHeight = random.nextDouble(0, 1.5);
                portal.getSource().getWorld().spawnParticle(configDataProvider.getPortalParticle(), LocationUtils.toCenterLocation(portal.getSource()).add(0, randomHeight, 0), 2);
            }
        }, 20, 1);
    }

    public void createPortalItem(PlayerDropItemEvent event) {
        ItemStack item = createBaseItem();
        ItemMeta meta = item.getItemMeta();

        UUID uuid = UUID.randomUUID();

        meta.getPersistentDataContainer().set(portalItemKey, PersistentDataType.STRING, uuid.toString());

        Location loc = LocationUtils.toCenterLocation(event.getItemDrop().getLocation().subtract(0, 1, 0));

        meta.setLore(configDataProvider.getPortalItemLore(loc)
                                       .stream()
                                       .map(x -> LegacyComponentSerializer.legacySection().serialize(x))
                                       .toList());

        item.setItemMeta(meta);
        event.getItemDrop().setItemStack(item);
        tempPortals.put(uuid, loc.add(0, 0.5, 0));

        event.getPlayer().spawnParticle(Particle.EXPLOSION, event.getItemDrop().getLocation(), 20);
    }

    public void updatePortalItemDestination(ItemStack itemStack, Location newDestination) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(configDataProvider.getPortalItemLore(newDestination)
                                       .stream()
                                       .map(x -> LegacyComponentSerializer.legacySection().serialize(x))
                                       .toList());
        itemStack.setItemMeta(meta);

        tempPortals.put(getUUIDFromItemStack(itemStack), newDestination);
    }

    public void createPortal(String playerName, ItemStack item, Location sourceLocation) {
        Location destLocation = tempPortals.get(getUUIDFromItemStack(item));

        db.createPortal(new Portal(UUID.randomUUID(),
                                   playerName,
                                   sourceLocation.getWorld().getName(),
                                   sourceLocation.getX(),
                                   sourceLocation.getY(),
                                   sourceLocation.getZ(),
                                   destLocation.getWorld().getName(),
                                   destLocation.getX(),
                                   destLocation.getY(),
                                   destLocation.getZ()));

        tempPortals.remove(getUUIDFromItemStack(item));
        updateCreatedPortalsVariable();
    }

    public boolean isPortalItem(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().get(portalItemKey, PersistentDataType.STRING) != null && tempPortals.containsKey(getUUIDFromItemStack(item));
    }

    public void checkIfPlayerIsInPortal(Player player, Location location) {
        Location loc = LocationUtils.toCenterLocation(location);
        Portal foundPortal = db.isExistingPortal(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());

        if(foundPortal == null)
            return;

        foundPortal.teleportToDestination(player);
        tempDisabledPortals.put(player, foundPortal.getDestination());
    }

    private UUID getUUIDFromItemStack(ItemStack itemStack) {
        if(!itemStack.hasItemMeta())
            return null;

        return UUID.fromString(itemStack.getItemMeta().getPersistentDataContainer().get(portalItemKey, PersistentDataType.STRING));
    }

    public Location getDestination(ItemStack itemStack) {
        return tempPortals.get(getUUIDFromItemStack(itemStack));
    }

    public boolean canCreatePortal(Player player) {
        if(player.isOp())
            return true;

        int maxPortals = configDataProvider.getDefaultMaxPortals();
        String basePermission = configDataProvider.getMaxPortalsBasePermission();

        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            if(!perm.getPermission().startsWith(basePermission))
                continue;

            try {
                var number = Integer.parseInt(Arrays.stream(perm.getPermission().split("\\.")).toList().getLast());

                if(number > maxPortals)
                    maxPortals = number;
            } catch (NumberFormatException ignored) {}
        }

        int currentPortals = db.getPortalCountByPlayerName(player.getName());

        return currentPortals < maxPortals;
    }

    public int getMaxPortalDistance(Player player) {
        if(player.isOp())
            return Integer.MAX_VALUE;

        int maxPortalDistance = configDataProvider.getDefaultMaxPortalDistance();
        String basePermission = configDataProvider.getMaxPortalDistanceBasePermission();

        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            if(!perm.getPermission().startsWith(basePermission))
                continue;

            try {
                var number = Integer.parseInt(Arrays.stream(perm.getPermission().split("\\.")).toList().getLast());

                if(number > maxPortalDistance)
                    maxPortalDistance = number;
            } catch (NumberFormatException ignored) {}
        }

        return maxPortalDistance;
    }

    public boolean isLocationAlreadyUsed(Location location) {
        return db.isExistingPortal(location.getWorld().getName(), location.getX(), location.getY(), location.getZ()) != null;
    }

    public void removePortal(Location location) {
        db.removePortalBySourceLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
        updateCreatedPortalsVariable();
    }

    public void reload() {
        UserTeleportPortals.getInstance().getConfigManager().reload();
        UserTeleportPortals.getInstance().getMessageManager().reload();
        updateCreatedPortalsVariable();
    }
}
