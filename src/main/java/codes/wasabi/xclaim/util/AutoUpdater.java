package codes.wasabi.xclaim.util;

import codes.wasabi.xclaim.XClaim;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

public final class AutoUpdater {

    public record UpdateOption(@NotNull String updateOption, @NotNull Callable<Void> runner) {
        public void update() throws Exception {
            runner.call();
        }
    }

    private static final Gson gson = new Gson();
    private static final Yaml yaml = new Yaml();
    private static boolean updated = false;

    public static @Nullable UpdateOption check() throws IOException {
        if (updated) return null;
        PluginDescriptionFile descriptionFile = XClaim.instance.getDescription();
        String pluginVersion = descriptionFile.getVersion();
        String apiVersion = descriptionFile.getAPIVersion();
        if (apiVersion == null) {
            apiVersion = Bukkit.getMinecraftVersion();
        }
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
                    if (!assetType.equalsIgnoreCase("application/x-java-archive")) continue;
                    String name = asset.get("name").getAsString();
                    String assetUrl = asset.get("browser_download_url").getAsString();
                    if (name.toLowerCase(Locale.ROOT).startsWith("original-")) continue;
                    assetName = name;
                    assetLink = assetUrl;
                    break;
                }
                if (assetLink == null) continue;
                URL configURL = new URL("https://raw.githubusercontent.com/WasabiThumb/xclaim/" + tagName + "/src/main/resources/plugin.yml");
                URLConnection cfgConn = configURL.openConnection();
                cfgConn.addRequestProperty("Accept", "text/plain; charset=utf-8");
                cfgConn.setDoInput(true);
                cfgConn.setDoOutput(false);
                cfgConn.connect();
                try (InputStream cfgIs = cfgConn.getInputStream()) {
                    Map<String, Object> map = yaml.load(cfgIs);
                    String api = (String) map.get("api-version");
                    if (!api.equalsIgnoreCase(apiVersion)) continue;
                } catch (Exception ignored) { }
                String finalLink = assetLink;
                String finalName = assetName;
                return new UpdateOption(tagName, () -> {
                    if (updated) return null;
                    File output = new File(Bukkit.getPluginsFolder(), finalName);
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

}
