package me.cooleg.funniewarsdetector.handling;

import me.cooleg.funniewarsdetector.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.time.Instant;
import java.time.temporal.ChronoField;

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
            if (config.getResponseMethod() == Config.ResponseMethod.BLOCK) {
                Component kickMessage = Component.text("FunniewarsDetector\n")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD).append(Component.text("You last played: " + instant + " UTC\n").color(NamedTextColor.YELLOW));
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickMessage);
            } else {
                Component alertMessage = Component.text("[FunniewarsDetector] ").color(NamedTextColor.BLUE)
                        .append(Component.text(event.getName() + " most recently played a Funniewars on ").color(NamedTextColor.WHITE)
                               .append(Component.text(monthToString(instant.get(ChronoField.MONTH_OF_YEAR)) + " " + instant.get(ChronoField.DAY_OF_MONTH)).color(NamedTextColor.YELLOW)));

                Bukkit.broadcast(alertMessage, "funniewarsdetector.alert");
            }
        }
    }

    private String monthToString(int index) {
        return switch (index) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "????";
        };
    }

}
