package dev.tomasgng.commands;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.config.dataprovider.ConfigDataProvider;
import dev.tomasgng.config.dataprovider.MessageDataProvider;
import dev.tomasgng.database.Portal;
import dev.tomasgng.utils.PortalCreator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class UserTeleportPortalsCommand implements CommandExecutor, TabCompleter {
    private final BukkitAudiences adventure = UserTeleportPortals.getInstance().getAdventure();
    private final ConfigDataProvider config = UserTeleportPortals.getInstance().getConfigDataProvider();
    private final MessageDataProvider messages = UserTeleportPortals.getInstance().getMessageDataProvider();
    private final PortalCreator portalCreator = UserTeleportPortals.getInstance().getPortalCreator();

    private Audience audience;
    private String[] args;
    private CommandSender sender;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        audience = adventure.sender(commandSender);
        this.args = args;
        sender = commandSender;

        if(isRemovePortalCmd())
            return false;

        if(isListPortalsCmd())
            return false;

        if(!commandSender.hasPermission(config.getCommandPermission())) {
            audience.sendMessage(messages.getCommandNoPermissions());
            return false;
        }

        // PERMISSION REQUIRED //

        if(isReloadCmd())
            return false;

        if(isUpdateCmd())
            return false;

        audience.sendMessage(messages.getCommandUsage());
        return false;
    }

    private boolean isReloadCmd() {
        if(args.length != 1)
            return false;

        if (!args[0].equalsIgnoreCase("reload"))
            return false;

        portalCreator.reload();
        audience.sendMessage(messages.getCommandReloadSuccess());
        return true;
    }

    private boolean isUpdateCmd() {
        if(args.length != 1)
            return false;

        if(!args[0].equalsIgnoreCase("update"))
            return false;

        if(UserTeleportPortals.getInstance().getVersionChecker().isLatestVersion()) {
            audience.sendMessage(messages.getCommandUpdateNoUpdateAvailable());
            return true;
        }

        UserTeleportPortals.getInstance().getVersionChecker().downloadNewestVersion(UserTeleportPortals.getInstance().getDescription().getName(), success -> {
            if(success)
                audience.sendMessage(messages.getCommandUpdateSuccess());
            else
                audience.sendMessage(messages.getCommandUpdateFailure());
        });
        return true;
    }

    // Internal Command, not for general player usage
    private boolean isRemovePortalCmd() {
        if(args.length != 2)
            return false;

        if(!args[0].equalsIgnoreCase("removeportal"))
            return false;

        UUID uuid;
        try {
            uuid = UUID.fromString(args[1]);
        } catch(IllegalArgumentException e) {
            return true;
        }

        Portal portal = portalCreator.getPortalById(uuid.toString());

        if(portal == null)
            return true;

        if(!portal.getPlayerName().equalsIgnoreCase(sender.getName()))
            return true;

        portalCreator.removePortalById(portal.getId().toString());
        audience.sendMessage(messages.getCommandRemovePortalSuccess());
        return true;
    }

    private boolean isListPortalsCmd() {
        if(args.length != 1)
            return false;

        if(!args[0].equalsIgnoreCase("listportals"))
            return false;

        if(!(sender instanceof Player player)) {
            sender.sendMessage("Player only command!");
            return true;
        }

        portalCreator.listPortals(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1)
            return List.of("reload", "update", "listportals");

        return List.of();
    }
}
