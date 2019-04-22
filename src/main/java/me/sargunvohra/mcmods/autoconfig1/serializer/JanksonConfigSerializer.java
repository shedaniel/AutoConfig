package me.sargunvohra.mcmods.autoconfig1.serializer;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.impl.SyntaxError;
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

    @SuppressWarnings("WeakerAccess")
    public JanksonConfigSerializer(Config definition, Class<T> configClass, Jankson jankson) {
        this.definition = definition;
        this.configClass = configClass;
        this.jankson = jankson;
    }

    public JanksonConfigSerializer(Config definition, Class<T> configClass) {
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
}
