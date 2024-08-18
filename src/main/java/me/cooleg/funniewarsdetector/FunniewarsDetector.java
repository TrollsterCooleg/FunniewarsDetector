package me.cooleg.funniewarsdetector;

import me.cooleg.funniewarsdetector.handling.APIUtils;
import me.cooleg.funniewarsdetector.handling.PlayerListener;
import me.cooleg.funniewarsdetector.handling.PlayerWhitelist;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class FunniewarsDetector extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config config = new Config();
        config.reloadConfig(this);

        PlayerWhitelist.loadWhitelist();
        APIUtils.startGlobalWhitelistUpdater();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(config), this);

        getCommand("funniewarsdetector").setExecutor(new MainCommand(config, this));
    }

}
