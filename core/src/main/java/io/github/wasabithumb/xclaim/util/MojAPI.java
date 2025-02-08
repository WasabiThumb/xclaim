package io.github.wasabithumb.xclaim.util;

import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Mojang API accessor.
 * Currently used for resolving player names that have not been seen before by the server.
 * @see #getProfile(String)
 */
public final class MojAPI {

    private static final String USER_AGENT = "xclaim; wasabithumbs@gmail.com";
    private static final String ACCEPT_JSON = "application/json";
    private static final int REQUEST_TIMEOUT = 60_000;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z_]{1,16}$");

    public static @NotNull MojAPI api(
            @NotNull Consumer<String> onWarn,
            @NotNull BiConsumer<String, Throwable> onError
    ) {
        return new MojAPI(new Out.Impl(onWarn, onError));
    }

    public static @NotNull MojAPI api(@NotNull Logger logger) {
        return new MojAPI(new Out.Std(logger));
    }

    //

    private final Out out;
    MojAPI(@NotNull Out out) {
        this.out = out;
    }

    public @Nullable UUID getProfile(@NotNull String name) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            this.out.warn("[MojAPI] Tried to resolve invalid username (" + name + ")");
            return null;
        }

        HttpURLConnection c = this.openGet("https://api.mojang.com/users/profiles/minecraft/" + name);
        if (c == null) return null;

        int responseCode = -1;
        try {
            responseCode = c.getResponseCode();
        } catch (IOException ignored) { }

        if (responseCode == 404 || responseCode == 204) {
            return null;
        } else if (responseCode != 200) {
            this.out.warn("[MojAPI] Unexpected response code: " + responseCode);
        }

        try {
            return this.getProfile0(c);
        } catch (IOException e) {
            this.out.error("[MojAPI] Broken pipe", e);
            return null;
        }
    }

    private @Nullable UUID getProfile0(@NotNull HttpURLConnection c) throws IOException {
        String errorMessage = null;

        try (InputStream is = c.getInputStream();
             InputStreamReader r = new InputStreamReader(is, StandardCharsets.UTF_8);
             JsonReader jr = new JsonReader(r)
        ) {
            jr.beginObject();

            String name;
            while (jr.hasNext()) {
                name = jr.nextName();
                if (name.equals("id")) {
                    return this.parseMojangUUID(jr.nextString());
                } else if (name.equals("errorMessage")) {
                    errorMessage = jr.nextString();
                }
            }

            jr.endObject();
        }

        if (errorMessage == null) {
            this.out.warn("[MojAPI] Response has no id or errorMessage");
        } else if (!errorMessage.equals("Couldn't find any profile with that name")) {
            this.out.warn("[MojAPI] Unexpected errorMessage: " + errorMessage);
        }
        return null;
    }

    private @Nullable UUID parseMojangUUID(@NotNull String data) {
        if (data.length() != 32) {
            this.reportInvalidUUID(data);
            return null;
        }

        long[] blocks = new long[4];
        for (int i=0; i < 4; i++) {
            long block = this.parseMojangUUIDBlock(data, i * 8);
            if (block == -1L) {
                this.reportInvalidUUID(data);
                return null;
            }
            blocks[i] = block;
        }

        return new UUID(
                (blocks[0] << 32) | blocks[1],
                (blocks[2] << 32) | blocks[3]
        );
    }

    private long parseMojangUUIDBlock(@NotNull String str, int offset) {
        long ret = 0;
        int nibble;
        for (int i=0; i < 8; i++) {
            ret <<= 4;
            nibble = Character.digit(str.charAt(offset + i), 16);
            if (nibble == -1) return -1L;
            ret |= nibble;
        }
        return ret;
    }

    private void reportInvalidUUID(@NotNull String data) {
        this.out.warn("[MojAPI] Received invalid UUID: " + data);
    }

    //

    private @Nullable HttpURLConnection openGet(@NotNull String uri) {
        URL url;
        try {
            url = URI.create(uri).toURL();
        } catch (IllegalArgumentException | MalformedURLException e) {
            this.out.error("[MojAPI] Cannot create request to invalid URL \"" + uri + "\"", e);
            return null;
        }

        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            this.out.error("[MojAPI] Failed to open connection to " + uri, e);
            return null;
        }

        try {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept", ACCEPT_JSON);
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setConnectTimeout(REQUEST_TIMEOUT);
        } catch (IOException e) {
            this.out.error("[MojAPI] Failed to set parameters for request to " + uri, e);
            return null;
        }

        try {
            conn.connect();
        } catch (IOException e) {
            this.out.error("[MojAPI] Connection was closed or timed out", e);
            return null;
        }

        return conn;
    }

    //

    private interface Out {

        void warn(@NotNull String message);

        void error(@NotNull String message, @NotNull Throwable thrown);

        //

        final class Impl implements Out {

            private final Consumer<String> onWarn;
            private final BiConsumer<String, Throwable> onError;

            Impl(@NotNull Consumer<String> onWarn, @NotNull BiConsumer<String, Throwable> onError) {
                this.onWarn = onWarn;
                this.onError = onError;
            }

            @Override
            public void warn(@NotNull String message) {
                this.onWarn.accept(message);
            }

            @Override
            public void error(@NotNull String message, @NotNull Throwable thrown) {
                this.onError.accept(message, thrown);
            }

        }

        final class Std implements Out {

            private final Logger handle;

            Std(@NotNull Logger handle) {
                this.handle = handle;
            }

            @Override
            public void warn(@NotNull String message) {
                this.handle.warning(message);
            }

            @Override
            public void error(@NotNull String message, @NotNull Throwable thrown) {
                this.handle.log(Level.WARNING, message, thrown);
            }

        }

    }

}
