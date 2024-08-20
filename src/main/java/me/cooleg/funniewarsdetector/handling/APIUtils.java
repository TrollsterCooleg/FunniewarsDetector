package me.cooleg.funniewarsdetector.handling;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIUtils {

    private static final Pattern pattern = Pattern.compile("\\{\"host\":\"[a-zA-Z0-9_]+\",\"date\":\"(.+?)\"");
    private static URL url;

    static {
        try {
            url = new URL("https://funniewarsdetectorwhitelist.pages.dev/globalwhitelist.txt");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Instant fetchLastPlayed(String name) {
        try {
            URL url = new URL("https://www.funniewars.live/profile/?search="+name);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);

            InputStream stream = connection.getInputStream();
            String string = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

            Matcher matcher = pattern.matcher(string);
            String group = null;
            while (matcher.find()) {group = matcher.group(1);}
            if (group == null) return null;

            ZonedDateTime date = ZonedDateTime.parse(group);
            return date.toInstant();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void startGlobalWhitelistUpdater() {
        new Thread(() -> {
            while (true) {
                Set<UUID> uuids = PlayerWhitelist.getGlobalWhitelist();

                try {
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setDoInput(true);

                    InputStream stream = connection.getInputStream();
                    Scanner scanner = new Scanner(stream);

                    uuids.clear();
                    while (scanner.hasNext()) {
                        String line = scanner.nextLine();
                        try {
                            UUID id = UUID.fromString(line.trim());
                            uuids.add(id);
                        } catch (IllegalArgumentException ignored) {
                            ignored.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}
