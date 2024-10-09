package dev.tomasgng.config.dataprovider;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.config.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
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

    public Component getDimensionPortalsMissingPermission() {
        return manager.getComponentValue(DIMENSION_PORTAL_MISSING_PERMISSION);
    }

    public Component getCommandNoPermissions() {
        return manager.getComponentValue(COMMAND_NO_PERMISSIONS);
    }

    public Component getCommandUsage() {
        return getComponentFromList(manager.getStringListValue(COMMAND_USAGE));
    }

    public Component getCommandReloadSuccess() {
        return manager.getComponentValue(COMMAND_RELOAD_SUCCESS);
    }

    public Component getCommandUpdateNoUpdateAvailable() {
        return manager.getComponentValue(COMMAND_UPDATE_NO_UPDATE_AVAILABLE);
    }

    public Component getCommandUpdateSuccess() {
        return manager.getComponentValue(COMMAND_UPDATE_SUCCESS);
    }

    public Component getCommandUpdateFailure() {
        return manager.getComponentValue(COMMAND_UPDATE_FAILURE);
    }

    public Component getCommandListPortalsHead(int portalLimit, int maxPortalDistance) {
        List<String> rawList = manager.getStringListValue(COMMAND_LISTPORTALS_HEADMSG);
        List<String> replacedPlaceholdersList = new ArrayList<>();

        for (String s : rawList) {
            s = replacePlaceholderRaw(s, "%portallimit%", portalLimit);
            s = replacePlaceholderRaw(s, "%maxportaldistance%", maxPortalDistance);
            replacedPlaceholdersList.add(s);
        }

        return getComponentFromList(replacedPlaceholdersList);
    }

    public Component getCommandListPortalsFormat(int number, String world, double x, double y, double z) {
        String strFormat = mm.serialize(manager.getComponentValue(COMMAND_LISTPORTALS_FORMAT));

        strFormat = replacePlaceholderRaw(strFormat, "%number%", number);
        strFormat = replacePlaceholderRaw(strFormat, "%world%", world);
        strFormat = replacePlaceholderRaw(strFormat, "%x%", x);
        strFormat = replacePlaceholderRaw(strFormat, "%y%", y);
        strFormat = replacePlaceholderRaw(strFormat, "%z%", z);

        return mm.deserialize(strFormat);
    }

    public Component getCommandRemovePortalSuccess() {
        return manager.getComponentValue(COMMAND_REMOVEPORTAL_SUCCESS);
    }

    private Component getComponentFromList(List<String> list) {
        Component msg = Component.empty();

        int lastIndex = list.size() - 1;
        for (int i = 0; i < list.size(); i++) {
            if(i == lastIndex)
                msg = msg.append(mm.deserializeOr(list.get(i), Component.empty()));
            else
                msg = msg.append(mm.deserializeOr(list.get(i), Component.empty())).appendNewline();
        }

        return msg;
    }

    private String replacePlaceholderRaw(String text, String search, Object replacement) {
        return text.replaceAll(search, replacement.toString());
    }

    private Component replacePlaceholder(String text, String search, String replacement) {
        return mm.deserialize(text.replaceAll(search, replacement));
    }
}
