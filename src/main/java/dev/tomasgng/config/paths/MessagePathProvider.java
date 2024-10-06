package dev.tomasgng.config.paths;

import dev.tomasgng.config.utils.ConfigPair;

import java.util.List;

public class MessagePathProvider {

    public static ConfigPair SOURCE_LOCATION_TOO_CLOSE_TO_DESTINATION = new ConfigPair("sourceLocationTooCloseToDestination", "<red>The source location is too close to the destination!");
    public static ConfigPair PORTAL_LIMIT_REACHED = new ConfigPair("portalLimitReached", "<red>You cannot create more portals. Destroy portals by breaking the block under the source location of any portal.");
    public static ConfigPair PORTAL_CREATED = new ConfigPair("portalCreated", "<green>You created a portal.");
    public static ConfigPair PORTAL_DESTINATION_CHANGED = new ConfigPair("portalDestinationChanged", "<green>You changed the portals destination.");
    public static ConfigPair SOURCE_LOCATION_BEING_USED = new ConfigPair("sourceLocationAlreadyBeingUsed", "<red>Portal cannot be created at this location.");
    public static ConfigPair PORTAL_DISTANCE_LIMIT_REACHED = new ConfigPair("maxPortalDistanceReached", "<red>The portal is too far away. The max portal distance is %maxportaldistance% blocks");

    public static ConfigPair COMMAND_NO_PERMISSIONS = new ConfigPair("command.noPermissions", "<red>You don't have permissions to execute this command.");
    public static ConfigPair COMMAND_USAGE = new ConfigPair("command.usage", List.of("<green>/utp reload <dark_gray>| <gray>Reload the config files",
                                                                                     "<green>/utp update <dark_gray>| <gray>Download the newest update",
                                                                                     "<green>/utp listportals <dark_gray>| <gray>Shows your current limits and your created portals"));

    public static ConfigPair COMMAND_RELOAD_SUCCESS = new ConfigPair("command.reloadSuccess", "<green>The config files have been reloaded successfully.");

    public static ConfigPair COMMAND_UPDATE_NO_UPDATE_AVAILABLE = new ConfigPair("command.updateNoUpdateAvailable", "<green>You are using the newest version.");
    public static ConfigPair COMMAND_UPDATE_SUCCESS = new ConfigPair("command.updateSuccess", "<green>The update has been downloaded. Restart the server to use the new version.");
    public static ConfigPair COMMAND_UPDATE_FAILURE = new ConfigPair("command.updateFailure", "<red>The download of the update has failed.");

    public static ConfigPair COMMAND_LISTPORTALS_HEADMSG = new ConfigPair("command.listPortals.headMsg", List.of("<b><green>Your Portals",
                                                                                                                 "<gray>Portal Limit: <yellow>%portallimit%",
                                                                                                                 "<gray>Max Portal Distance: <yellow>%maxportaldistance%",
                                                                                                                 "",
                                                                                                                 "<red>Leftclick to remove a portal"));
    public static ConfigPair COMMAND_LISTPORTALS_FORMAT = new ConfigPair("command.listPortals.format", "<gray>%number% <dark_gray>| <yellow>%world%<dark_gray>[<yellow>%x%<dark_gray>, <yellow>%y%<dark_gray>, <yellow>%z%<dark_gray>]");

    public static ConfigPair COMMAND_REMOVEPORTAL_SUCCESS = new ConfigPair("command.removePortal", "<green>Portal has been removed.");
}
