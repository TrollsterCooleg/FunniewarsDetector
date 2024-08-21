package me.cooleg.funniewarsdetector;

import me.cooleg.funniewarsdetector.handling.PlayerWhitelist;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class MainCommand implements CommandExecutor {

    private static final String HINT = ChatColor.GREEN + "Invalid arguments! Options are reload, whitelist [player], unwhitelist [player].";
    private static final String RELOADED = ChatColor.GREEN + "Reloaded config!";
    private static final String SPECIFY_PLAYER = ChatColor.RED + "You must specify a player's username!";
    private static final String INVALID = ChatColor.RED + "This player has never joined this server! Please send the players full UUID to whitelist this player.";
    private static final String SUCCESS_WHITELIST = ChatColor.GREEN + "Successfully whitelisted the player!";
    private static final String SUCCESS_UNWHITELIST = ChatColor.GREEN + "Successfully unwhitelisted the player!";

    private final Config config;
    private final FunniewarsDetector funniewarsDetector;

    public MainCommand(Config config, FunniewarsDetector funniewarsDetector) {
        this.config = config;
        this.funniewarsDetector = funniewarsDetector;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length < 1) {sender.sendMessage(HINT); return true;}
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                funniewarsDetector.reloadConfig();
                config.reloadConfig(funniewarsDetector);
                sender.sendMessage(RELOADED);
            }
            case "whitelist" -> {
                if (args.length < 2) {sender.sendMessage(SPECIFY_PLAYER); return true;}
                UUID id = uuidFromString(args[1]);
                if (id == null) {sender.sendMessage(INVALID); return true;}

                PlayerWhitelist.addToWhitelist(id);
                sender.sendMessage(SUCCESS_WHITELIST);
            }
            case "unwhitelist" -> {
                if (args.length < 2) {sender.sendMessage(SPECIFY_PLAYER); return true;}
                UUID id = uuidFromString(args[1]);
                if (id == null) {sender.sendMessage(INVALID); return true;}

                PlayerWhitelist.removeFromWhitelist(id);
                sender.sendMessage(SUCCESS_UNWHITELIST);
            }
            default -> sender.sendMessage(HINT);
        }

        return true;
    }

    private UUID uuidFromString(String string) {
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(string);
        UUID id;

        if (player == null) {
            try {
                id = UUID.fromString(string);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        } else {
            id = player.getUniqueId();
        }

        return id;
    }
}
