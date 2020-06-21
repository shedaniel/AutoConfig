package me.sargunvohra.mcmods.autoconfig1u.serializer;

import com.google.gson.*;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.util.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This serializer serializes configs into Json files using Gson.
 */
@SuppressWarnings("unused")
public class GsonConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {

    private Config definition;
    private Class<T> configClass;
    private Gson gson;

    @SuppressWarnings("WeakerAccess")
    public GsonConfigSerializer(Config definition, Class<T> configClass, Gson gson) {
        this.definition = definition;
        this.configClass = configClass;
        this.gson = gson;
    }

    public GsonConfigSerializer(Config definition, Class<T> configClass) {
        this(definition, configClass, new GsonBuilder().registerTypeAdapter(InputUtil.KeyCode.class, new KeyCodeJsonAdapter())
            .setPrettyPrinting().create());
    }

    private Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDirectory().toPath().resolve(definition.name() + ".json");
    }

    @Override
    public void serialize(T config) throws SerializationException {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            BufferedWriter writer = Files.newBufferedWriter(configPath);
            gson.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public T deserialize() throws SerializationException {
        Path configPath = getConfigPath();
        if (Files.exists(configPath)) {
            try {
                BufferedReader reader = Files.newBufferedReader(configPath);
                T ret = gson.fromJson(reader, configClass);
                reader.close();
                return ret;
            } catch (IOException | JsonParseException e) {
                throw new SerializationException(e);
            }
        } else {
            return createDefault();
        }
    }

    @Override
    public T createDefault() {
        return Utils.constructUnsafely(configClass);
    }

    // I'm pretty sure Gson can serialize KeyCode just fine? but just in case...
    // besides, this is technically an optimization - KeyCode also has a "name" field that's safe to ignore
    // - ADudeCalledLeo
    public static class KeyCodeJsonAdapter implements JsonSerializer<InputUtil.KeyCode>,
                                                      JsonDeserializer<InputUtil.KeyCode> {
        @Override
        public JsonElement serialize(InputUtil.KeyCode src, Type srcT, JsonSerializationContext ctx) {
            final JsonObject jObj = new JsonObject();
            jObj.add("type", ctx.serialize(src.getCategory()));
            jObj.add("code", ctx.serialize(src.getKeyCode(), Integer.class)); // why u converting ints to floats
            return jObj;
        }

        @Override
        public InputUtil.KeyCode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx)
            throws JsonParseException {
            final JsonObject jObj = json.getAsJsonObject();
            final InputUtil.Type type = ctx.deserialize(jObj.get("type"), InputUtil.Type.class);
            final int code = jObj.get("code").getAsInt();
            return type.createFromCode(code);
        }
    }
}
