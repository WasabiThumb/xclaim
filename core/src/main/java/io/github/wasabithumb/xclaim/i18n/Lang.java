package io.github.wasabithumb.xclaim.i18n;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Lang {

    private static final String FALLBACK_MESSAGE = "<red><i>???</i></red>";
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    private final String id;
    private final Map<String, LangString> map;
    public Lang(@NotNull String id) {
        this.id = id;
        this.map = new HashMap<>();
    }

    public @NotNull String id() {
        return this.id;
    }

    public boolean load(@NotNull JsonObject ob) {
        boolean any = false;
        for (Map.Entry<String, JsonElement> entry : ob.entrySet()) {
            if (this.map.containsKey(entry.getKey())) continue;
            JsonElement value = entry.getValue();
            if (!value.isJsonPrimitive()) continue;
            JsonPrimitive primitive = value.getAsJsonPrimitive();
            any = true;
            this.map.put(entry.getKey(), LangString.parse(primitive.getAsString()));
        }
        return any;
    }

    public boolean load(@NotNull InputStream is) throws IOException {
        try (InputStreamReader isReader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return this.load(GSON.fromJson(isReader, JsonObject.class));
        }
    }

    public @NotNull String get(@NotNull String key, @NotNull String @NotNull ... args) {
        LangString data = this.map.get(key);
        if (data == null) return FALLBACK_MESSAGE;
        return data.resolve(args);
    }

    public @NotNull String get(@NotNull String key, @NotNull Object @NotNull ... args) {
        String[] str = new String[args.length];
        for (int i=0; i < args.length; i++) str[i] = Objects.toString(args[i]);
        return this.get(key, str);
    }

    public void serialize(@NotNull OutputStream os) throws IOException {
        JsonObject object = new JsonObject();
        for (Map.Entry<String, LangString> entry : this.map.entrySet()) {
            object.addProperty(entry.getKey(), entry.getValue().serialize());
        }

        try (OutputStreamWriter osWriter = new OutputStreamWriter(os, StandardCharsets.UTF_8);
             JsonWriter jsonWriter = new JsonWriter(osWriter)
        ) {
            GSON.toJson(object, JsonObject.class, jsonWriter);
        }
    }

}
