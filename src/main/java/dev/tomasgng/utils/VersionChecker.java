package dev.tomasgng.utils;

import dev.tomasgng.UserTeleportPortals;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class VersionChecker {

    private final Logger logger = UserTeleportPortals.getInstance().getLogger();
    private String currentVersion;
    private CheckMode checkMode;
    private long pluginId = -1;
    private URL url;

    public VersionChecker(String currentVersion, CheckMode mode, long pluginId) {
        this.currentVersion = currentVersion;
        this.checkMode = mode;
        this.pluginId = pluginId;
    }

    public VersionChecker(String currentVersion, CheckMode mode, String url) {
        this.currentVersion = currentVersion;
        this.checkMode = mode;
        try {
            this.url = URI.create(url).toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the current version being used is the latest version available.
     * @return true if the current version is the latest version available. Otherwise, false.
     */
    public boolean isLatestVersion() {
        if(checkMode == CheckMode.SPIGET && pluginId > 0) {
            try {
                InputStream inputStream = createSpigetInputStream();

                if(inputStream == null)
                    return false;

                boolean isLatestVersion = getLatestVersion(inputStream).equalsIgnoreCase(currentVersion);
                inputStream.close();

                return isLatestVersion;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(checkMode == CheckMode.URL && url != null) {
            try {
                InputStream inputStream = url.openStream();
                String latestVersion = getLatestVersion(inputStream);

                if(latestVersion == null || latestVersion.isBlank())
                    return false;

                boolean isLatestVersion = latestVersion.equalsIgnoreCase(currentVersion);
                inputStream.close();

                return isLatestVersion;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Downloads the newest version available on Spigot.<br>
     * Saves it to the <code>plugins/update/</code> folder. <br>
     * Will work if a pluginId is given (see Constructor).
     * @param filename The filename of the downloaded file.
     * @param afterCompletion The actions after the download succeed or failed. A boolean is given as a result.
     */
    public void downloadNewestVersion(String filename, Consumer<Boolean> afterCompletion) {
        Bukkit.getScheduler().runTaskAsynchronously(UserTeleportPortals.getInstance(), () -> {
            if(checkMode != CheckMode.SPIGET && pluginId < 1) {
                afterCompletion.accept(false);
                throw new UnsupportedOperationException("This method works with the Spiget checkmode. PluginID must be greater than 0.");
            }

            try(InputStream inputStream = new URI("https://api.spiget.org/v2/resources/" + pluginId + "/download").toURL().openStream()) {
                new File("plugins/update").mkdirs();
                Files.copy(inputStream, Paths.get("plugins/update", filename + ".jar"), StandardCopyOption.REPLACE_EXISTING);
                afterCompletion.accept(true);
            } catch (IOException | URISyntaxException e) {
                afterCompletion.accept(false);
                logger.severe("Failed to download the update:");
                e.printStackTrace();
            }
        });
    }

    private InputStream createSpigetInputStream() {
        try {
            URL spigetUrl = new URI("https://api.spiget.org/v2/resources/" + pluginId + "/versions/latest").toURL();
            return spigetUrl.openStream();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getLatestVersion(InputStream in) {
        if(checkMode == CheckMode.SPIGET) {
            String jsonSource = getStringFromInputStream(in);

            if(jsonSource == null)
                return "ERROR";

            JSONObject jsonObject = new JSONObject(jsonSource);
            return jsonObject.getString("name");
        }
        if(checkMode == CheckMode.URL) {
            return getStringFromInputStream(in);
        }
        return "ERROR";
    }

    private String getStringFromInputStream(InputStream inputStream){
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }

            return result.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * The method used for the version checking.
     * <p>
     * "SPIGET": Check for the newest version with the Spiget API.<br>
     * "URL": Check for the newest version by reading the content of the given URL.
     */
    public enum CheckMode {
        SPIGET,
        URL
    }
}



