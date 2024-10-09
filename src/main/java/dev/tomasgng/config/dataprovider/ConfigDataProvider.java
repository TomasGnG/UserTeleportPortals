package dev.tomasgng.config.dataprovider;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static dev.tomasgng.config.paths.ConfigPathProvider.*;

public class ConfigDataProvider {

    private final Logger logger = UserTeleportPortals.getInstance().getLogger();
    private final ConfigManager config = UserTeleportPortals.getInstance().getConfigManager();
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final DecimalFormat df = new DecimalFormat("#.##");

    public String getCommandPermission() {
        if(config.getString(COMMAND_PERMISSION).isBlank()) {
            logger.warning("Command Permission is not valid! Using default permission => " + COMMAND_PERMISSION.getStringValue());
            return COMMAND_PERMISSION.getStringValue();
        }

        return config.getString(COMMAND_PERMISSION);
    }

    public int getDefaultMaxPortals() {
        return config.getInteger(DEFAULT_MAX_PORTALS);
    }

    public String getMaxPortalsBasePermission() {
        return config.getString(MAX_PORTALS_BASE_PERMISSION);
    }

    public int getDefaultMaxPortalDistance() {
        return config.getInteger(DEFAULT_MAX_PORTAL_DISTANCE);
    }

    public String getMaxPortalDistanceBasePermission() {
        return config.getString(MAX_PORTAL_DISTANCE_PERMISSION);
    }

    public String getDimensionPortalPermission() {
        return config.getString(DIMENSION_PORTAL_PERMISSION);
    }

    public int getPortalMinimumDistance() {
        return config.getInteger(PORTAL_MINIMUM_DISTANCE);
    }

    public Material getPortalItemMaterial() {
        Material material = Material.getMaterial(config.getString(PORTAL_ITEM_MATERIAL).toUpperCase());

        if(material == null) {
            logger.severe(config.getString(PORTAL_ITEM_MATERIAL) + " is not a valid material! Path: " + PORTAL_ITEM_MATERIAL.getPath());
            return Material.getMaterial(PORTAL_ITEM_MATERIAL.getStringValue());
        }

        return material;
    }

    public Component getPortalItemDisplayname() {
        return config.getComponentValue(PORTAL_ITEM_DISPLAYNAME);
    }

    public List<Component> getPortalItemLore(Location location) {
        List<String> unformattedLoreList = config.getStringList(PORTAL_ITEM_LORE);
        List<Component> lore = getLoreCore(unformattedLoreList, location);

        if(lore.equals(Component.empty())) {
            return getLoreCore(PORTAL_ITEM_LORE.getStringListValue(), location);
        }

        return lore;
    }

    private List<Component> getLoreCore(List<String> list, Location location) {
        List<Map.Entry<String, String>> placeholders = new ArrayList<>();
        placeholders.add(new AbstractMap.SimpleEntry<>("%portalblock%", getPortalBlockMaterial().name()));
        placeholders.add(new AbstractMap.SimpleEntry<>("%world%", location.getWorld().getName()));
        placeholders.add(new AbstractMap.SimpleEntry<>("%x%", df.format(location.getX())));
        placeholders.add(new AbstractMap.SimpleEntry<>("%y%", df.format(location.getY())));
        placeholders.add(new AbstractMap.SimpleEntry<>("%z%", df.format(location.getZ())));

        List<String> formattedList = new ArrayList<>();

        list.forEach(x -> formattedList.add(replacePlacerholders(x, placeholders)));

        return formattedList.stream()
                   .filter(x -> mm.deserializeOrNull(x) != null)
                   .map(mm::deserialize)
                   .toList();
    }

    public Material getPortalBlockMaterial() {
        Material material = Material.getMaterial(config.getString(PORTAL_BLOCK_MATERIAL).toUpperCase());

        if(material == null || !material.isBlock()) {
            logger.severe(config.getString(PORTAL_BLOCK_MATERIAL) + " is not a valid block! Path: " + PORTAL_BLOCK_MATERIAL.getPath());
            return Material.getMaterial(PORTAL_BLOCK_MATERIAL.getStringValue());
        }

        return material;
    }

    public Particle getPortalParticle() {
        Particle particle;

        try {
            particle = Particle.valueOf(config.getString(PORTAL_PARTICLE).toUpperCase());
        } catch (IllegalArgumentException ignored) {
            logger.severe(config.getString(PORTAL_PARTICLE) + " is not a valid particle! Path: " + PORTAL_PARTICLE.getPath());
            return Particle.valueOf(PORTAL_PARTICLE.getStringValue());
        }

        return particle;
    }

    private String replacePlacerholders(String text, List<Map.Entry<String, String>> placeholders) {
        for (Map.Entry placeholder : placeholders) {
            text = text.replace(placeholder.getKey().toString(), placeholder.getValue().toString());
        }

        return text;
    }
}
