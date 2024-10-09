package dev.tomasgng.config.paths;

import dev.tomasgng.config.utils.ConfigExclude;
import dev.tomasgng.config.utils.ConfigPair;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.List;

public class ConfigPathProvider {
    public static ConfigPair COMMAND_PERMISSION = new ConfigPair("commandPermission", "utp.cmd.use", "The permission to access the /utp command");
    public static ConfigPair DEFAULT_MAX_PORTALS = new ConfigPair("defaultMaxPortals", 3);
    public static ConfigPair MAX_PORTALS_BASE_PERMISSION = new ConfigPair("maxPortalsBasePermission", "utp.portals",
                                                                          "The base permission to set max portals per player with permissions",
                                                                          "'utp.portals.5' would set the max portals of a player to 5",
                                                                          "IMPORTANT! Please use a number after the last '.' otherwise the plugin can't recognize it!");
    public static ConfigPair DEFAULT_MAX_PORTAL_DISTANCE = new ConfigPair("defaultMaxPortalDistance", 50);
    public static ConfigPair MAX_PORTAL_DISTANCE_PERMISSION = new ConfigPair("maxPortalDistanceBasePermission", "utp.portaldistance",
                                                                          "The base permission to set the max portal distance in the same world per player",
                                                                          "'utp.portaldistance.100' would set the max portal distance of a player to 100 blocks",
                                                                          "IMPORTANT! Please use a number after the last '.' otherwise the plugin can't recognize it!");
    public static ConfigPair DIMENSION_PORTAL_PERMISSION = new ConfigPair("dimensionPortalPermission", "utp.dimensionportal", "Players with this permission can create Portals through different worlds.");
    public static ConfigPair PORTAL_MINIMUM_DISTANCE = new ConfigPair("portalMinDistance", 3, "The minimum distance between the source and destination in blocks.", "'sourceLocationTooCloseToDestination' message will be sent if the source location is below the value.");
    public static ConfigPair PORTAL_BLOCK_MATERIAL = new ConfigPair("portalBlockMaterial", Material.CRYING_OBSIDIAN.name(), "The block where the portals can be created");
    public static ConfigPair PORTAL_PARTICLE = new ConfigPair("portalParticle", Particle.PORTAL.name(), "The particle that is shown for portals.");
    @ConfigExclude
    public static ConfigPair PORTAL_ITEM_BASE = new ConfigPair("portalItem", null, "Customize the item which is used for creating a portal item");
    public static ConfigPair PORTAL_ITEM_MATERIAL = new ConfigPair(PORTAL_ITEM_BASE.getPath() + ".material", Material.ENDER_PEARL.name());
    public static ConfigPair PORTAL_ITEM_DISPLAYNAME = new ConfigPair(PORTAL_ITEM_BASE.getPath() + ".displayname", "<green>Portal Creator");
    public static ConfigPair PORTAL_ITEM_LORE = new ConfigPair(PORTAL_ITEM_BASE.getPath() + ".lore", List.of("<gray>Rightclick <yellow>%portalblock% <gray>to set the source location.",
                                                                                                                    "<gray>Rightclick any <yellow>other block <gray>to change the destination location.",
                                                                                                                    "",
                                                                                                                    "<green>Destination Location: <dark_green>%world% (%x%, %y%, %z%)"));
}
