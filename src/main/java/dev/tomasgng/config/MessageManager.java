package dev.tomasgng.config;

import dev.tomasgng.UserTeleportPortals;
import dev.tomasgng.config.paths.MessagePathProvider;
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

public class MessageManager {
    private final File folder = new File("plugins/UserTeleportPortals");
    private final File configFile = new File("plugins/UserTeleportPortals/messages.yml");

    private final MiniMessage mm = MiniMessage.miniMessage();
    private YamlConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);

    public MessageManager() {
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

        pathProviders.add(MessagePathProvider.class);

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

        pathProviders.add(MessagePathProvider.class);

        pathProviders.forEach(pathProvider -> {
            List<Field> fieldList = Arrays.stream(pathProvider.getDeclaredFields()).filter(field -> Modifier.isStatic(field.getModifiers())).toList();
            for (Field field : fieldList) {
                try {
                    ConfigPair configPair = (ConfigPair) field.get(ConfigPair.class);

                    if(cfg.isSet(configPair.getPath()))
                        continue;

                    if(field.getAnnotation(ConfigExclude.class) == null) {
                        configPairs.add(configPair);
                    }
                    else {
                        ConfigExclude configExclude = field.getAnnotation(ConfigExclude.class);

                        if(!configExclude.excludeComments())
                            commentConfigPairs.add(configPair);
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

    public Object getObjectValue(ConfigPair pair) {
        return cfg.get(pair.getPath(), pair.getValue());
    }

    public String getStringValue(ConfigPair pair) {
        return cfg.getString(pair.getPath(), pair.getStringValue());
    }

    public boolean getBooleanValue(ConfigPair pair) {
        return cfg.getBoolean(pair.getPath(), pair.getBooleanValue());
    }

    public int getIntegerValue(ConfigPair pair) {
        return cfg.getInt(pair.getPath(), pair.getIntegerValue());
    }

    public List<String> getStringListValue(ConfigPair pair) {
        return cfg.getStringList(pair.getPath());
    }

    public Component getComponentValue(ConfigPair pair) {
        String value = getStringValue(pair);

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
        cfg.setComments(pair.getPath(), pair.getComments());
    }

    public void set(ConfigPair pair, Object newValue) {
        cfg.set(pair.getPath(), newValue);
        save();
    }

}
