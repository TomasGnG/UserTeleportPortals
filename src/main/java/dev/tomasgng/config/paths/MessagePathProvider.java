package dev.tomasgng.config.paths;

import dev.tomasgng.config.utils.ConfigPair;

import java.util.List;

public class MessagePathProvider {

    public static ConfigPair SOURCE_LOCATION_TOO_CLOSE_TO_DESTINATION = new ConfigPair("sourceLocationTooCloseToDestination", "<red>The source location is too close to the destination!");
    public static ConfigPair PORTAL_LIMIT_REACHED = new ConfigPair("portalLimitReached", "<red>You cannot create more portals. Destroy portals by breaking the block under the source location of any portal.");
    public static ConfigPair PORTAL_CREATED = new ConfigPair("portalCreated", "<green>You created a portal.");
    public static ConfigPair PORTAL_DESTINATION_CHANGED = new ConfigPair("portalDestinationChanged", "<green>You changed the portals destination.");
    public static ConfigPair SOURCE_LOCATION_BEING_USED = new ConfigPair("sourceLocationAlreadyBeingUsed", "<red>Portal cannot be created at this location.");
    public static ConfigPair COMMAND_NO_PERMISSIONS = new ConfigPair("command.noPermissions", "<red>You don't have permissions to execute this command.");
    public static ConfigPair COMMAND_USAGE = new ConfigPair("command.usage", List.of("<green>/utp reload <dark_gray>| <gray>Reload the config files"));
    public static ConfigPair COMMAND_RELOAD_SUCCESS = new ConfigPair("command.reloadSuccess", "<green>The config files have been reloaded successfully.");
}
