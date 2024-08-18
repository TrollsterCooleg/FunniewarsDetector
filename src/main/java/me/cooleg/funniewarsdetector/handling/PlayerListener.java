package me.cooleg.funniewarsdetector.handling;

import me.cooleg.funniewarsdetector.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.time.Instant;

public class PlayerListener implements Listener {

    private final Config config;

    public PlayerListener(Config config) {
        this.config = config;
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void asyncLogin(AsyncPlayerPreLoginEvent event) {
        if (PlayerWhitelist.isWhitelisted(event.getUniqueId())) return;
        Instant instant = APIUtils.fetchLastPlayed(event.getName());
        if (instant == null) {return;}

        if (instant.plusSeconds(config.getRecoverySeconds()).isAfter(Instant.now())) {
            Component kickMessage = Component.text("FunniewarsDetector\n")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD).append(Component.text("You last played: " + instant + " UTC\n").color(NamedTextColor.YELLOW));
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickMessage);
        }
    }

}
