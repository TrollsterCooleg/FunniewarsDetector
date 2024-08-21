package me.cooleg.funniewarsdetector;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class Config {
    private static final int CONFIG_VER = 1;

    private int recoverySeconds;
    private ResponseMethod responseMethod;

    public void reloadConfig(FunniewarsDetector plugin) {
        FileConfiguration config = plugin.getConfig();
        int ver = config.getInt("config-ver", 0);
        if (ver != CONFIG_VER) {
            config = updateConfig(plugin);
        }

        recoverySeconds = config.getInt("acceptable-recovery-period-seconds", 604800);
        responseMethod = config.getString("response-method", "block").trim()
                .equalsIgnoreCase("alert") ? ResponseMethod.ALERT : ResponseMethod.BLOCK;
    }

    public int getRecoverySeconds() {
        return recoverySeconds;
    }

    public ResponseMethod getResponseMethod() {
        return responseMethod;
    }

    public enum ResponseMethod {
        BLOCK,
        ALERT;
    }


    private FileConfiguration updateConfig(JavaPlugin plugin) {
        // Fetch all current values
        FileConfiguration config = plugin.getConfig();
        Map<String, Object> values = config.getValues(true);

        // Update file to new file
        try (InputStream stream = Config.class.getResourceAsStream("/config.yml")) {
            if (stream == null) throw new RuntimeException("[" + plugin.getName() + "] Failed to load config file in jar.");

            plugin.getConfig().load(new InputStreamReader(stream));
        } catch (IOException | InvalidConfigurationException ex) {
            throw new RuntimeException("[" + plugin.getName() + "] Failed to load updated config version.");
        }

        // Set values for options to what they were before.
        config = plugin.getConfig();
        for (Map.Entry<String, Object> value : values.entrySet()) {
            // Ignore values that were removed in current config version, and leave new config version as it is.
            if (!config.isSet(value.getKey()) || value.getKey().equals("config-ver")) continue;

            config.set(value.getKey(), value.getValue());
        }

        try {
            config.save(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
        } catch (IOException e) {
            throw new RuntimeException("[" + plugin.getName() + "] Failed to save updated config version.");
        }

        return config;
    }

}
