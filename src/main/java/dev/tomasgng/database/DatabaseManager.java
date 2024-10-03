package dev.tomasgng.database;

import dev.tomasgng.UserTeleportPortals;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final File folder = new File("plugins/UserTeleportPortals/data");

    private Connection asyncConn;
    private Connection conn;

    public DatabaseManager() {
        initConnection();
    }

    private void initConnection() {
        folder.mkdirs();

        try {
            DriverManager.getDriver("jdbc:sqlite:");
            conn = DriverManager.getConnection("jdbc:sqlite:" + folder.getPath() + "/portals.db");
            asyncConn = DriverManager.getConnection("jdbc:sqlite:" + folder.getPath() + "/portals.db");
            UserTeleportPortals.getInstance().getLogger().info("Connected to database.");

            initDatabase();
        } catch (SQLException e) {
            UserTeleportPortals.getInstance().getLogger().severe("Failed to connect to database. Error: " + e.getMessage());
        }
    }

    private void initDatabase() {
        try {
            String table = Portal.TABLE_NAME;

            String sql = "CREATE TABLE IF NOT EXISTS " + table + " (" +
                    "id VARCHAR(36) PRIMARY KEY, " +
                    Portal.FIELD_PLAYER_NAME + " VARCHAR(50), " +
                    Portal.FIELD_SOURCE_WORLD + " VARCHAR(50), " +
                    Portal.FIELD_X + " DOUBLE, " +
                    Portal.FIELD_Y + " DOUBLE, " +
                    Portal.FIELD_Z + " DOUBLE, " +
                    Portal.FIELD_DEST_WORLD + " VARCHAR(50), " +
                    Portal.FIELD_DEST_X + " DOUBLE, " +
                    Portal.FIELD_DEST_Y + " DOUBLE, " +
                    Portal.FIELD_DEST_Z + " DOUBLE" +
                    ")";

            PreparedStatement stm = conn.prepareStatement(sql);
            stm.executeUpdate();
            stm.close();

            UserTeleportPortals.getInstance().getLogger().info("Initialized database successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Portal> getPortals() {
        List<Portal> portals = new ArrayList<>();

        try {
            String sql = "SELECT * FROM " + Portal.TABLE_NAME;

            PreparedStatement stm = asyncConn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Portal portal = new Portal(UUID.fromString(rs.getString(1)),
                                           rs.getString(2),
                                           rs.getString(3),
                                           rs.getDouble(4),
                                           rs.getDouble(5),
                                           rs.getDouble(6),
                                           rs.getString(7),
                                           rs.getDouble(8),
                                           rs.getDouble(9),
                                           rs.getDouble(10));

                portals.add(portal);
            }

            rs.close();
            stm.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return portals;
    }

    public void createPortal(Portal portal) {
        try {
            String sql = "INSERT INTO " + Portal.TABLE_NAME + " VALUES(?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, portal.getId().toString());
            stm.setString(2, portal.getPlayerName());
            stm.setString(3, portal.getSource().getWorld().getName());
            stm.setDouble(4, portal.getSource().getX());
            stm.setDouble(5, portal.getSource().getY());
            stm.setDouble(6, portal.getSource().getZ());
            stm.setString(7, portal.getDestination().getWorld().getName());
            stm.setDouble(8, portal.getDestination().getX());
            stm.setDouble(9, portal.getDestination().getY());
            stm.setDouble(10, portal.getDestination().getZ());

            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Portal isExistingPortal(String sourceWorld, double x, double y, double z) {
        try {
            String sql = "SELECT * FROM " + Portal.TABLE_NAME + " WHERE " +
                    Portal.FIELD_SOURCE_WORLD + "=? AND " +
                    Portal.FIELD_X + "=? AND " +
                    Portal.FIELD_Y + "=? AND " +
                    Portal.FIELD_Z + "=?";

            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, sourceWorld);
            stm.setDouble(2, x);
            stm.setDouble(3, y);
            stm.setDouble(4, z);

            ResultSet rs = stm.executeQuery();

            if(rs.next()) {
                Portal portal = new Portal(UUID.fromString(rs.getString(1)),
                                           rs.getString(2),
                                           rs.getString(3),
                                           rs.getDouble(4),
                                           rs.getDouble(5),
                                           rs.getDouble(6),
                                           rs.getString(7),
                                           rs.getDouble(8),
                                           rs.getDouble(9),
                                           rs.getDouble(10));
                stm.close();
                return portal;
            } else {
                stm.close();
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPortalCountByPlayerName(String playerName) {
        try {
            String sql = "SELECT COUNT(*) FROM " + Portal.TABLE_NAME + " WHERE " + Portal.FIELD_PLAYER_NAME + "=?";

            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, playerName);

            int count = stm.executeQuery().getInt(1);

            stm.close();
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePortalBySourceLocation(String world, double x, double y, double z) {
        try {
            String sql = "DELETE FROM " + Portal.TABLE_NAME + " WHERE " +
                    Portal.FIELD_SOURCE_WORLD + "=? AND " +
                    Portal.FIELD_X + "=? AND " +
                    Portal.FIELD_Y + "=? AND " +
                    Portal.FIELD_Z + "=?";

            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, world);
            stm.setDouble(2, x);
            stm.setDouble(3, y);
            stm.setDouble(4, z);
            stm.executeUpdate();
            stm.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
