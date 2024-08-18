package me.cooleg.funniewarsdetector.handling;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerWhitelist {

    private static YamlConfiguration yaml;
    private static List<UUID> whitelisted;
    private static Set<UUID> globalWhitelist = ConcurrentHashMap.newKeySet();
    private static final File whitelist = new File("plugins/FunniewarsDetector/whitelist.yml");
    private static final Path path = Path.of("plugins/FunniewarsDetector/whitelist.yml");
    private static final InputStream whitelistStream = PlayerWhitelist.class.getResourceAsStream("/whitelist.yml");

    public static void addToWhitelist(UUID id) {
        if (whitelisted.contains(id)) return;
        whitelisted.add(id);
        yaml.set("whitelisted-uuids", whitelisted.stream().map(UUID::toString).collect(Collectors.toList()));

        CompletableFuture.runAsync(() -> {
            try {
                yaml.save(whitelist);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void removeFromWhitelist(UUID id) {
        if (!whitelisted.contains(id)) return;
        whitelisted.remove(id);
        yaml.set("whitelisted-uuids", whitelisted.stream().map(UUID::toString).collect(Collectors.toList()));

        CompletableFuture.runAsync(() -> {
            try {
                yaml.save(whitelist);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static boolean isWhitelisted(UUID id) {
        return whitelisted.contains(id) || globalWhitelist.contains(id);
    }

    public static void loadWhitelist()  {
        if (!whitelist.exists()) {
            try {
                Files.copy(whitelistStream, path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        }

        yaml = YamlConfiguration.loadConfiguration(whitelist);
        whitelisted = yaml.getStringList("whitelisted-uuids").stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public static List<UUID> getWhitelist() {
        return whitelisted;
    }

    public static Set<UUID> getGlobalWhitelist() {
        return globalWhitelist;
    }

}
