package me.sargunvohra.mcmods.autoconfig1u.serializer;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.util.Utils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This serializer serializes configs into Toml files using Toml4j.
 */
@SuppressWarnings("unused")
public class Toml4jConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {

    private Config definition;
    private Class<T> configClass;
    private TomlWriter tomlWriter;

    @SuppressWarnings("WeakerAccess")
    public Toml4jConfigSerializer(Config definition, Class<T> configClass, TomlWriter tomlWriter) {
        this.definition = definition;
        this.configClass = configClass;
        this.tomlWriter = tomlWriter;
    }

    public Toml4jConfigSerializer(Config definition, Class<T> configClass) {
        this(definition, configClass, new TomlWriter());
    }

    private Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDirectory().toPath().resolve(definition.name() + ".toml");
    }

    @Override
    public void serialize(T config) throws SerializationException {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            tomlWriter.write(config, configPath.toFile());
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public T deserialize() throws SerializationException {
        Path configPath = getConfigPath();
        if (Files.exists(configPath)) {
            try {
                return new Toml().read(configPath.toFile()).to(configClass);
            } catch (IllegalStateException e) {
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
