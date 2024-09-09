package dev.tomasgng.utils;

import org.bukkit.Location;

public class LocationUtils {

    public static Location toCenterLocation(Location loc) {
        Location location = loc.clone();

        location.setX(loc.getBlockX() + 0.5);
        location.setY(loc.getBlockY() + 0.5);
        location.setZ(loc.getBlockZ() + 0.5);

        return location;
    }

}
