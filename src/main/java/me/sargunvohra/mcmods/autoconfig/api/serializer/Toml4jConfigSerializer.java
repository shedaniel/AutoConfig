package me.sargunvohra.mcmods.autoconfig.api.serializer;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigSerializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

public class Toml4jConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {

    private String name;
    private Class<T> configClass;

    public Toml4jConfigSerializer(String name, Class<T> configClass) {
        this.name = name;
        this.configClass = configClass;
    }

    private Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDirectory().toPath().resolve(name + ".toml");
    }

    @Override
    public void serialize(T config) throws SerializationException {
        try {
            TomlWriter writer = new TomlWriter();
            writer.write(config, getConfigPath().toFile());
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
        try {
            Constructor<T> constructor = configClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
