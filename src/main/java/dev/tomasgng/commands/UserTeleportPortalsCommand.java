package dev.tomasgng.commands;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.config.dataprovider.ConfigDataProvider;
import dev.tomasgng.config.dataprovider.MessageDataProvider;
import dev.tomasgng.utils.PortalCreator;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UserTeleportPortalsCommand implements CommandExecutor, TabCompleter {
    private final BukkitAudiences adventure = UserTeleportPortals.getInstance().getAdventure();
    private final ConfigDataProvider config = UserTeleportPortals.getInstance().getConfigDataProvider();
    private final MessageDataProvider messages = UserTeleportPortals.getInstance().getMessageDataProvider();
    private final PortalCreator portalCreator = UserTeleportPortals.getInstance().getPortalCreator();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission(config.getCommandPermission())) {
            adventure.sender(sender).sendMessage(messages.getCommandNoPermissions());
            return false;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                portalCreator.reload();
                adventure.sender(sender).sendMessage(messages.getCommandReloadSuccess());
                return false;
            }
            if(args[0].equalsIgnoreCase("update")) {
                if(UserTeleportPortals.getInstance().getVersionChecker().isLatestVersion()) {
                    adventure.sender(sender).sendMessage(messages.getCommandUpdateNoUpdateAvailable());
                    return false;
                }
                UserTeleportPortals.getInstance().getVersionChecker().downloadNewestVersion(UserTeleportPortals.getInstance().getDescription().getName(), success -> {
                    if(success)
                        adventure.sender(sender).sendMessage(messages.getCommandUpdateSuccess());
                    else
                        adventure.sender(sender).sendMessage(messages.getCommandUpdateFailure());
                });
                return false;
            }
        }

        adventure.sender(sender).sendMessage(messages.getCommandUsage());
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1)
            return List.of("reload", "update");

        return List.of();
    }
}
