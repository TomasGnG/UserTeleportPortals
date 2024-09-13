package dev.tomasgng.config.dataprovider;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.config.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

import static dev.tomasgng.config.paths.MessagePathProvider.*;

public class MessageDataProvider {

    private final MessageManager manager = UserTeleportPortals.getInstance().getMessageManager();
    private final MiniMessage mm = MiniMessage.miniMessage();

    public Component getSourceLocationTooCloseToDestination() {
        return manager.getComponentValue(SOURCE_LOCATION_TOO_CLOSE_TO_DESTINATION);
    }

    public Component getPortalLimitReached() {
        return manager.getComponentValue(PORTAL_LIMIT_REACHED);
    }

    public Component getPortalCreated() {
        return manager.getComponentValue(PORTAL_CREATED);
    }

    public Component getPortalDestinationChanged() {
        return manager.getComponentValue(PORTAL_DESTINATION_CHANGED);
    }

    public Component getSourceLocationAlreadyBeingUsed() {
        return manager.getComponentValue(SOURCE_LOCATION_BEING_USED);
    }

    public Component getMaxPortalDistanceReached(int maxPortalDistance) {
        return replacePlaceholder(manager.getStringValue(PORTAL_DISTANCE_LIMIT_REACHED), "%maxportaldistance%", maxPortalDistance + "");
    }

    public Component getCommandNoPermissions() {
        return manager.getComponentValue(COMMAND_NO_PERMISSIONS);
    }

    public Component getCommandUsage() {
        Component msg = Component.empty();
        List<String> rawList = manager.getStringListValue(COMMAND_USAGE);
        int lastIndex = rawList.size() - 1;

        for (int i = 0; i < rawList.size(); i++) {
            if(i == lastIndex) {
                msg = msg.append(mm.deserializeOr(rawList.get(i), Component.empty()));
            } else
                msg = msg.append(mm.deserializeOr(rawList.get(i), Component.empty())).appendNewline();
        }

        return msg;
    }

    public Component getCommandReloadSuccess() {
        return manager.getComponentValue(COMMAND_RELOAD_SUCCESS);
    }

    private Component replacePlaceholder(String text, String search, String replacement) {
        return mm.deserialize(text.replaceAll(search, replacement));
    }
}
