package codes.wasabi.xclaim.util;

import codes.wasabi.xclaim.platform.Platform;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
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
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, "UTF-8"));
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
                byte[] bytes = StreamUtil.readAllBytes(is);
                String string = new String(bytes, StandardCharsets.UTF_8);
                JsonObject ob = gson.fromJson(string, JsonObject.class);
                String id = ob.get("id").getAsString();
                return Bukkit.getOfflinePlayer(parseMojangUUID(id));
            } else if (code == 204 || code == 400 || code == 404) {
                return null;
            } else if (code == 405) {
                throw new IllegalStateException("GET method is unsupported for this endpoint!");
            } else if (code != 429) {
                throw new IllegalStateException("Unknown error " + code + " occurred during HTTP GET");
            }
        } catch (IOException ignored) { }
        return null;
    }

    private static @NotNull UUID parseMojangUUID(@NotNull String str) throws IllegalArgumentException {
        if (str.length() != 32) throwInvalidUUID(str, "length != 32");
        long[] blocks = new long[4];

        for (int i=0; i < 4; i++) {
            if ((blocks[i] = parseMojangUUIDBlock(str, i * 8)) == -1L)
                throwInvalidUUID(str, "malformed block " + i);
        }

        return new UUID(
                ((blocks[0] << 32) | blocks[1]),
                ((blocks[2] << 32) | blocks[3])
        );
    }

    private static long parseMojangUUIDBlock(@NotNull String str, int offset) {
        long ret = 0;
        char c;
        for (int i=0; i < 8; i++) {
            ret <<= 4;
            c = str.charAt(offset | i);

            if ('0' <= c && c <= '9') {
                ret |= (((long) c) - 48L);
            } else if ('a' <= c && c <= 'f') {
                ret |= (((long) c) - 87L);
            } else if ('A' <= c && c <= 'F') {
                ret |= (((long) c) - 55L);
            } else {
                return -1L;
            }
        }
        return ret;
    }

    @Contract("_, _ -> fail")
    private static void throwInvalidUUID(@NotNull String str, @NotNull String detail) throws IllegalArgumentException {
        String msg = "Invalid UUID string \"" + str + "\" (" + detail + ")";
        throw new IllegalStateException(msg);
    }

}
