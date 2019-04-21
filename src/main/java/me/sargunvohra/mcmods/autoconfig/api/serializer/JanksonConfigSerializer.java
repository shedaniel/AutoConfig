package me.sargunvohra.mcmods.autoconfig.api.serializer;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.impl.SyntaxError;
import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigSerializer;
import me.sargunvohra.mcmods.autoconfig.impl.Utils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

public class JanksonConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {

    private String name;
    private Class<T> configClass;
    private Jankson jankson;

    @SuppressWarnings("WeakerAccess")
    public JanksonConfigSerializer(String name, Class<T> configClass, Jankson jankson) {
        this.name = name;
        this.configClass = configClass;
        this.jankson = jankson;
    }

    public JanksonConfigSerializer(String name, Class<T> configClass) {
        this(name, configClass, Jankson.builder().build());
    }

    private Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDirectory().toPath().resolve(name + ".json5");
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
