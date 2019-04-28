package me.sargunvohra.mcmods.autoconfig1.serializer;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.impl.SyntaxError;
import com.google.gson.Gson;
import me.sargunvohra.mcmods.autoconfig1.ConfigData;
import me.sargunvohra.mcmods.autoconfig1.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1.util.Utils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This serializer serializes configs into Json5 files using Jankson.
 */
@SuppressWarnings("unused")
public class JanksonConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {

    private Config definition;
    private Class<T> configClass;
    private Jankson jankson;

    // we need a gson to work around jankson's fromJson bug
    private Gson gson;

    @Deprecated
    public JanksonConfigSerializer(Config definition, Class<T> configClass, Jankson jankson, Gson gson) {
        this.definition = definition;
        this.configClass = configClass;
        this.jankson = jankson;
        this.gson = gson;
    }

    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public JanksonConfigSerializer(Config definition, Class<T> configClass, Jankson jankson) {
        this(definition, configClass, jankson, new Gson());
    }

    public JanksonConfigSerializer(Config definition, Class<T> configClass) {
        //noinspection deprecation
        this(definition, configClass, Jankson.builder().build());
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
                return gson.fromJson(jankson.load(getConfigPath().toFile()).toJson(false, false), configClass);
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
}
