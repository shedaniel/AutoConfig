package me.sargunvohra.mcmods.autoconfig.impl;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigHolder;
import me.sargunvohra.mcmods.autoconfig.api.ConfigSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigManager<T extends ConfigData> implements ConfigHolder<T> {

    private final Logger logger;
    private final String name;
    private final Class<T> configClass;
    private final ConfigSerializer<T> serializer;

    private T config;

    public ConfigManager(String name, Class<T> configClass, ConfigSerializer<T> serializer) {
        logger = LogManager.getLogger();

        this.name = name;
        this.configClass = configClass;
        this.serializer = serializer;

        if (load()) {
            save();
        }
    }

    String getName() {
        return name;
    }

    Class<T> getConfigClass() {
        return configClass;
    }

    ConfigSerializer<T> getSerializer() {
        return serializer;
    }

    void save() {
        try {
            serializer.serialize(config);
        } catch (ConfigSerializer.SerializationException e) {
            logger.error("Failed to save config '{}'", name, e);
        }
    }

    private boolean load() {
        try {
            config = serializer.deserialize();
            config.validatePostLoad();
            return true;
        } catch (ConfigSerializer.SerializationException | ConfigData.ValidationException e) {
            logger.error("Failed to load config '{}', using default!", name, e);
            config = serializer.createDefault();
            try {
                config.validatePostLoad();
            } catch (ConfigData.ValidationException v) {
                throw new RuntimeException("result of createDefault() was invalid!", v);
            }
            return false;
        }
    }

    @Override
    public T getConfig() {
        return config;
    }
}
