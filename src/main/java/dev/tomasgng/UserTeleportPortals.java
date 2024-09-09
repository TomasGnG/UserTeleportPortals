package dev.tomasgng;

import dev.tomasgng.commands.UserTeleportPortalsCommand;
import dev.tomasgng.config.ConfigManager;
import dev.tomasgng.config.MessageManager;
import dev.tomasgng.config.dataprovider.ConfigDataProvider;
import dev.tomasgng.config.dataprovider.MessageDataProvider;
import dev.tomasgng.database.DatabaseManager;
import dev.tomasgng.listeners.*;
import dev.tomasgng.utils.PortalCreator;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class UserTeleportPortals extends JavaPlugin {

    private static UserTeleportPortals instance;

    private ConfigManager configManager;
    private MessageManager messageManager;
    private DatabaseManager databaseManager;
    private ConfigDataProvider configDataProvider;
    private MessageDataProvider messageDataProvider;
    private PortalCreator portalCreator;
    private BukkitAudiences adventure;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager();
        messageManager = new MessageManager();
        databaseManager = new DatabaseManager();
        configDataProvider = new ConfigDataProvider();
        messageDataProvider = new MessageDataProvider();
        portalCreator = new PortalCreator();

        adventure = BukkitAudiences.create(this);

        getCommand("userteleportportals").setExecutor(new UserTeleportPortalsCommand());
        getCommand("userteleportportals").setTabCompleter(new UserTeleportPortalsCommand());

        PluginManager m = Bukkit.getPluginManager();

        m.registerEvents(new PlayerDropItemListener(), this);
        m.registerEvents(new PlayerInteractListener(), this);
        m.registerEvents(new PlayerMoveListener(), this);
        m.registerEvents(new BlockBreakListener(), this);
        m.registerEvents(new BlockPlaceListener(), this);
    }

    @Override
    public void onDisable() {
        if(adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    public static UserTeleportPortals getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PortalCreator getPortalCreator() {
        return portalCreator;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ConfigDataProvider getConfigDataProvider() {
        return configDataProvider;
    }

    public MessageDataProvider getMessageDataProvider() {
        return messageDataProvider;
    }

    public BukkitAudiences getAdventure() {
        return adventure;
    }
}
