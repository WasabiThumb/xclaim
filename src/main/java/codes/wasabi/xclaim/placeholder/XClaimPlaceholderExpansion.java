package codes.wasabi.xclaim.placeholder;

import codes.wasabi.xclaim.XClaim;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class XClaimPlaceholderExpansion extends PlaceholderExpansion {

    private final XClaim plugin;
    private final Map<String, Placeholder> withoutArgMap;
    private final Map<String, Placeholder> withArgMap;
    public XClaimPlaceholderExpansion(XClaim plugin) {
        this.plugin = plugin;

        Map<String, Placeholder> withoutArgMap = new HashMap<>();
        Map<String, Placeholder> withArgMap = new HashMap<>();
        for (Placeholder p : Placeholders.values()) {
            String stem = p.getStem().toLowerCase(Locale.ROOT);
            if (p.hasPositionalArgument()) {
                withArgMap.put(stem, p);
            } else {
                withoutArgMap.put(stem, p);
            }
        }
        this.withoutArgMap = Collections.unmodifiableMap(withoutArgMap);
        this.withArgMap = Collections.unmodifiableMap(withArgMap);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "xclaim";
    }

    @Override
    public @NotNull String getAuthor() {
        return "WasabiThumbs @ Contributors";
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        final int len = params.length();
        final String lower = params.toLowerCase(Locale.ROOT);
        char c;
        for (int i=(len - 1); i >= 0; i--) {
            c = lower.charAt(i);
            if (c == '_') {
                Placeholder withArg = this.withArgMap.get(lower.substring(0, i));
                if (withArg != null) return withArg.computeFor(player, params.substring(i + 1));
            }
        }

        Placeholder withoutArg = this.withoutArgMap.get(lower);
        if (withoutArg != null) return withoutArg.computeFor(player, null);
        return null;
    }

}
