package dev.tomasgng.config;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.config.paths.ConfigPathProvider;
import dev.tomasgng.config.utils.ConfigExclude;
import dev.tomasgng.config.utils.ConfigPair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ConfigManager {
    private final File folder = new File("plugins/UserTeleportPortals");
    private final File configFile = new File("plugins/UserTeleportPortals/config.yml");

    private YamlConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ConfigManager() {
        createFiles();
    }

    private void createFiles() {
        if(!folder.exists())
            folder.mkdirs();

        if(configFile.exists()) {
            setMissingConfigPaths();
            save();
            return;
        }

        try {
            configFile.createNewFile();

            reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setAllConfigPaths();
        save();
    }

    private void setAllConfigPaths() {
        List<ConfigPair> configPairs = new ArrayList<>();
        List<ConfigPair> commentConfigPairs = new ArrayList<>();
        List<Class> pathProviders = new ArrayList<>();

        pathProviders.add(ConfigPathProvider.class);

        pathProviders.forEach(pathProvider -> {
            List<Field> fieldList = Arrays.stream(pathProvider.getDeclaredFields()).filter(field -> Modifier.isStatic(field.getModifiers())).toList();
            for (Field field : fieldList) {
                try {
                    if(field.getAnnotation(ConfigExclude.class) == null)
                        configPairs.add((ConfigPair) field.get(ConfigPair.class));
                    else {
                        ConfigExclude configExclude = field.getAnnotation(ConfigExclude.class);

                        if(!configExclude.excludeComments())
                            commentConfigPairs.add((ConfigPair) field.get(ConfigPair.class));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        configPairs.forEach(this::set);
        commentConfigPairs.forEach(this::setComments);
    }

    private void setMissingConfigPaths() {
        List<ConfigPair> configPairs = new ArrayList<>();
        List<ConfigPair> commentConfigPairs = new ArrayList<>();
        List<Class> pathProviders = new ArrayList<>();

        pathProviders.add(ConfigPathProvider.class);

        pathProviders.forEach(pathProvider -> {
            List<Field> fieldList = Arrays.stream(pathProvider.getDeclaredFields()).filter(field -> Modifier.isStatic(field.getModifiers())).toList();
            for (Field field : fieldList) {
                try {
                    ConfigPair pair = (ConfigPair) field.get(ConfigPair.class);

                    if(field.getAnnotation(ConfigExclude.class) == null) {
                        if(field.getName().contains("EXAMPLE")) {
                            if(!cfg.isSet(pair.getPath().split("\\.")[0]))
                                configPairs.add(pair);
                        } else if(!cfg.isSet(pair.getPath()))
                            configPairs.add(pair);
                    } else {
                        ConfigExclude configExclude = field.getAnnotation(ConfigExclude.class);

                        if(!configExclude.excludeComments() && !cfg.isSet(pair.getPath()))
                            commentConfigPairs.add((ConfigPair) field.get(ConfigPair.class));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        configPairs.forEach(this::set);
        commentConfigPairs.forEach(this::setComments);
    }

    public void reload() {
        cfg = YamlConfiguration.loadConfiguration(configFile);
    }

    private void save() {
        try {
            cfg.save(configFile);
            reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getObject(ConfigPair pair) {
        return cfg.get(pair.getPath(), pair.getValue());
    }

    public String getString(ConfigPair pair) {
        return cfg.getString(pair.getPath(), pair.getStringValue());
    }

    public boolean getBoolean(ConfigPair pair) {
        return cfg.getBoolean(pair.getPath(), pair.getBooleanValue());
    }

    public int getInteger(ConfigPair pair) {
        return cfg.getInt(pair.getPath(), pair.getIntegerValue());
    }

    public List<String> getStringList(ConfigPair pair) {
        return cfg.getStringList(pair.getPath());
    }

    public Component getComponentValue(ConfigPair pair) {
        String value = getString(pair);

        try {
            return mm.deserialize(value);
        } catch (Exception e) {
            UserTeleportPortals.getInstance().getLogger().log(Level.WARNING, "The message {" + value + "} is not in MiniMessage format! Source (" + pair.getPath() + ")" + System.lineSeparator() + e.getMessage());
            return mm.deserialize(pair.getStringValue());
        }
    }

    private void set(ConfigPair pair) {
        cfg.set(pair.getPath(), pair.getValue());

        if(pair.hasComments())
            cfg.setComments(pair.getPath(), pair.getComments());
    }

    private void setComments(ConfigPair pair) {
        if(pair.hasComments())
            cfg.setComments(pair.getPath(), pair.getComments());
    }

    public void set(ConfigPair pair, Object newValue) {
        cfg.set(pair.getPath(), newValue);
        save();
    }
}
