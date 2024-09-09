package dev.tomasgng.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Portal {
    public static final String TABLE_NAME = "portals";
    public static final String FIELD_PLAYER_NAME = "playerName";
    public static final String FIELD_SOURCE_WORLD = "sourceWorld";
    public static final String FIELD_X = "x";
    public static final String FIELD_Y = "y";
    public static final String FIELD_Z = "z";
    public static final String FIELD_DEST_WORLD = "destWorld";
    public static final String FIELD_DEST_X = "destX";
    public static final String FIELD_DEST_Y = "destY";
    public static final String FIELD_DEST_Z = "destZ";

    private final UUID id;
    private final String playerName;
    private final String sourceWorld;
    private final double x;
    private final double y;
    private final double z;
    private final String destWorld;
    private final double destX;
    private final double destY;
    private final double destZ;

    public Portal(UUID id, String playerName, String sourceWorld, double x, double y, double z, String destWorld, double destX, double destY, double destZ) {
        this.id = id;
        this.playerName = playerName;
        this.sourceWorld = sourceWorld;
        this.x = x;
        this.y = y;
        this.z = z;
        this.destWorld = destWorld;
        this.destX = destX;
        this.destY = destY;
        this.destZ = destZ;
    }

    public UUID getId() {
        return id;
    }

    public Location getSource() {
        World world = Bukkit.getWorld(sourceWorld);

        if(world == null)
            return null;

        return new Location(world, x, y, z);
    }

    public Location getDestination() {
        World world = Bukkit.getWorld(destWorld);

        if(world == null)
            return null;

        return new Location(world, destX, destY, destZ);
    }

    public void teleportToDestination(Player player) {
        player.teleport(getDestination());
    }

    public String getPlayerName() {
        return playerName;
    }
}
