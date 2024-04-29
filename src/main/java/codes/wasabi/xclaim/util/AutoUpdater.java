package codes.wasabi.xclaim.util;

import codes.wasabi.xclaim.XClaim;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;

public final class AutoUpdater {

    public static class UpdateOption {
        private final String updateOption;
        private final Callable<Void> runner;

        public UpdateOption(@NotNull String updateOption, @NotNull Callable<Void> runner) {
            this.updateOption = updateOption;
            this.runner = runner;
        }

        public final @NotNull String updateOption() {
            return updateOption;
        }

        public final @NotNull Callable<Void> runner() {
            return runner;
        }

        public void update() throws Exception {
            runner.call();
        }

        @Override
        public int hashCode() {
            return Objects.hash(updateOption, runner);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (obj instanceof UpdateOption) {
                UpdateOption other = (UpdateOption) obj;
                if (Objects.equals(updateOption, other.updateOption)) {
                    if (Objects.equals(runner, other.runner)) return true;
                }
            }
            return super.equals(obj);
        }

        @Override
        public String toString() {
            return "UpdateOption[updateOption=" + updateOption + ",runner=" + runner + "]";
        }
    }

    private static final Gson gson = new Gson();
    private static boolean updated = false;

    public static @Nullable UpdateOption check() throws IOException {
        if (updated) return null;
        PluginDescriptionFile descriptionFile = XClaim.instance.getDescription();
        String pluginVersion = descriptionFile.getVersion();
        URL listEndpoint = new URL("https://api.github.com/repos/WasabiThumb/xclaim/releases");
        URLConnection conn = listEndpoint.openConnection();
        conn.addRequestProperty("Accept", "application/json; charset=utf-8");
        conn.setDoInput(true);
        conn.setDoOutput(false);
        conn.connect();
        try (InputStream is = conn.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            JsonArray array = gson.fromJson(reader, JsonArray.class);
            for (int i=0; i < array.size(); i++) {
                JsonObject entry = array.get(i).getAsJsonObject();
                String tagName = entry.get("tag_name").getAsString();
                if (tagName.equalsIgnoreCase(pluginVersion)) return null;
                String assetLink = null;
                String assetName = "";
                JsonArray assets = entry.get("assets").getAsJsonArray();
                for (int z=0; z < assets.size(); z++) {
                    JsonObject asset = assets.get(z).getAsJsonObject();
                    String assetType = asset.get("content_type").getAsString();
                    if (!checkMime(assetType)) continue;
                    String name = asset.get("name").getAsString();
                    String assetUrl = asset.get("browser_download_url").getAsString();
                    if (name.toLowerCase(Locale.ROOT).startsWith("original-")) continue;
                    assetName = name;
                    assetLink = assetUrl;
                    break;
                }
                if (assetLink == null) continue;
                final String finalLink = assetLink;
                final String finalName = assetName;
                return new UpdateOption(tagName, () -> {
                    if (updated) return null;
                    File pluginsFolder = XClaim.jarFile.getParentFile();
                    File output = new File(pluginsFolder, finalName);
                    if (output.exists()) {
                        throw new IOException("File " + finalName + " already exists!");
                    }
                    if (!output.createNewFile()) {
                        throw new IOException("Failed to create " + finalName);
                    }
                    try (FileOutputStream fos = new FileOutputStream(output)) {
                        URL downloadURL = new URL(finalLink);
                        URLConnection downloadConnection = downloadURL.openConnection();
                        downloadConnection.addRequestProperty("Accept", "application/x-java-archive");
                        downloadConnection.setDoInput(true);
                        downloadConnection.setDoOutput(false);
                        try (InputStream cis = downloadConnection.getInputStream()) {
                            IOUtils.copyLarge(cis, fos);
                        }
                    }
                    if (!XClaim.jarFile.delete()) throw new IOException("Failed to delete existing jar file!");
                    XClaim.jarFile = output;
                    updated = true;
                    return null;
                });
            }
        }
        return null;
    }

    private static final String MIME_PARTS_A = "application/";
    private static final String MIME_PARTS_B = "java-archive";
    private static boolean checkMime(String mime) {
        final int len = mime.length();
        if (len < 24) return false;
        if (!mime.startsWith(MIME_PARTS_A)) return false;
        int h = 12;
        if (mime.charAt(h) == 'x') {
            h++;
            if (mime.charAt(h++) != '-') return false;
        }
        return mime.regionMatches(h, MIME_PARTS_B, 0, 12);
    }

}
