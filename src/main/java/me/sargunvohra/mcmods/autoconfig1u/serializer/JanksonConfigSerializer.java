package me.sargunvohra.mcmods.autoconfig1u.serializer;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.Marshaller;
import blue.endless.jankson.impl.SyntaxError;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.util.Utils;
import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This serializer serializes configs into Json5 files using Jankson.
 */
@SuppressWarnings("unused")
public class JanksonConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {

    private Config definition;
    private Class<T> configClass;
    private Jankson jankson;

    public JanksonConfigSerializer(Config definition, Class<T> configClass, Jankson jankson) {
        this.definition = definition;
        this.configClass = configClass;
        this.jankson = jankson;
    }

    public JanksonConfigSerializer(Config definition, Class<T> configClass) {
        this(definition, configClass, Jankson.builder()
            .registerTypeFactory(InputUtil.KeyCode.class, () -> InputUtil.UNKNOWN_KEYCODE)
            .registerTypeAdapter(InputUtil.KeyCode.class, new KeyCodeJsonTypeAdapter())
            .registerSerializer(InputUtil.KeyCode.class, new KeyCodeJsonSerializer())
            .registerTypeFactory(ModifierKeyCode.class, ModifierKeyCode::unknown)
            .registerTypeAdapter(ModifierKeyCode.class, new ModifierKeyCodeTypeAdapter())
            .registerSerializer(ModifierKeyCode.class, new ModifierKeyCodeJsonSerializer())
            .build());
    }

    private Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDirectory().toPath().resolve(definition.name() + ".json5");
    }

    @Override
    public void serialize(T config) throws SerializationException {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            BufferedWriter writer = Files.newBufferedWriter(configPath);
            writer.write(jankson.toJson(config).toJson(true, true));
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
                return jankson.fromJson(jankson.load(getConfigPath().toFile()), configClass);
            } catch (IOException | SyntaxError e) {
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

    // not sure how Jankson handles (de)serialization errors exactly, I'll just assume a try/catch?
    // hopefully this won't break horribly
    // - ADudeCalledLeo
    private static class KeyCodeJsonTypeAdapter implements Function<JsonObject, InputUtil.KeyCode> {
        @Override
        public InputUtil.KeyCode apply(JsonObject jsonObject) {
            final InputUtil.Type type = jsonObject.get(InputUtil.Type.class, "type");
            final int code = jsonObject.get(Integer.class, "code");
            return type.createFromCode(code);
        }
    }
    private static class KeyCodeJsonSerializer implements BiFunction<InputUtil.KeyCode, Marshaller, JsonElement> {
        @Override
        public JsonElement apply(InputUtil.KeyCode keyCode, Marshaller marshaller) {
            final JsonObject jObj = new JsonObject();
            jObj.put("type", marshaller.serialize(keyCode.getCategory()));
            jObj.put("code", marshaller.serialize(keyCode.getKeyCode()));
            return jObj;
        }
    }

    private static class ModifierKeyCodeTypeAdapter implements Function<JsonObject, ModifierKeyCode> {
        @Override
        public ModifierKeyCode apply(JsonObject jsonObject) {
            final InputUtil.Type type = jsonObject.get(InputUtil.Type.class, "type");
            final int code = jsonObject.get(Integer.class, "code");
            final Modifier mod = Modifier.of(jsonObject.get(Short.class, "mod"));
            return ModifierKeyCode.of(type.createFromCode(code), mod);
        }
    }
    private static class ModifierKeyCodeJsonSerializer implements BiFunction<ModifierKeyCode, Marshaller, JsonElement> {
        @Override
        public JsonElement apply(ModifierKeyCode keyCode, Marshaller marshaller) {
            final JsonObject jObj = new JsonObject();
            jObj.put("type", marshaller.serialize(keyCode.getType()));
            jObj.put("code", marshaller.serialize(keyCode.getKeyCode()));
            jObj.put("mod", marshaller.serialize(keyCode.getModifier().getValue()));
            return jObj;
        }
    }
}
