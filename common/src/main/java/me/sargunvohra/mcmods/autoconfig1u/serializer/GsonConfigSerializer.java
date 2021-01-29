package me.sargunvohra.mcmods.autoconfig1u.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.util.Utils;
import me.shedaniel.architectury.platform.Platform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This serializer serializes configs into Json files using Gson.
 */
@SuppressWarnings({"unused", "FieldMayBeFinal"})
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
        this(definition, configClass, new GsonBuilder().setPrettyPrinting().create());
    }
    
    private Path getConfigPath() {
        return Platform.getConfigFolder().resolve(definition.name() + ".json");
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
}
