package codes.wasabi.xclaim.util;

import codes.wasabi.xclaim.platform.Platform;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class NameToPlayer {

    private static final Gson gson = new Gson();
    private static final int REQUEST_TIMEOUT = 2000;

    public static @Nullable OfflinePlayer getPlayer(@NotNull String name) {
        OfflinePlayer ply = Bukkit.getPlayer(name);
        if (ply != null) return ply;
        ply = Platform.get().getOfflinePlayerIfCached(name);
        if (ply != null) return ply;
        // Must make a web request to Mojang APIs now
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, StandardCharsets.UTF_8));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setDoOutput(false);
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(REQUEST_TIMEOUT);
            conn.connect();

            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                byte[] bytes = is.readAllBytes();
                String string = new String(bytes, StandardCharsets.UTF_8);
                JsonObject ob = gson.fromJson(string, JsonObject.class);
                String id = ob.get("id").getAsString();
                String uuidString = id.substring(0, 8) + "-"
                        + id.substring(8, 12) + "-"
                        + id.substring(12, 16) + "-"
                        + id.substring(16, 20) + "-"
                        + id.substring(20);
                UUID uuid = UUID.fromString(uuidString);
                return Bukkit.getOfflinePlayer(uuid);
            } else if (code == 204 || code == 400) {
                return null;
            } else if (code == 405) {
                throw new IllegalStateException("GET method is unsupported for this endpoint!");
            } else if (code != 429) {
                throw new IllegalStateException("Unknown error " + code + " occurred during HTTP GET");
            }
        } catch (IOException ignored) { }
        return null;
    }

}
