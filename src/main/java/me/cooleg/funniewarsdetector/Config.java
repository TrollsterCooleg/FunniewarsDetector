package me.cooleg.funniewarsdetector;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private int recoverySeconds;

    public void reloadConfig(FunniewarsDetector plugin) {
        FileConfiguration config = plugin.getConfig();
        recoverySeconds = config.getInt("acceptable-recovery-period-seconds", 604800);
    }

    public int getRecoverySeconds() {
        return recoverySeconds;
    }

}
