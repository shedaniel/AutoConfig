package me.sargunvohra.mcmods.autoconfig1u;

import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.event.ConfigChangedEvent;
import me.sargunvohra.mcmods.autoconfig1u.serializer.ConfigSerializer;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigManager<T extends ConfigData> implements ConfigHolder<T> {

    private final Logger logger;

    private final Config definition;
    private final Class<T> configClass;
    private final ConfigSerializer<T> serializer;

    private T config;

    ConfigManager(Config definition, Class<T> configClass, ConfigSerializer<T> serializer) {
        logger = LogManager.getLogger();

        this.definition = definition;
        this.configClass = configClass;
        this.serializer = serializer;

        if (load()) {
            save();
        }
    }

    public Config getDefinition() {
        return definition;
    }

    public Class<T> getConfigClass() {
        return configClass;
    }

    public ConfigSerializer<T> getSerializer() {
        return serializer;
    }

    public void save() {
        ActionResult result = ConfigChangedEvent.SAVED.invoker().onSave(this);
        if(result == ActionResult.FAIL) {
            return;
        }
        try {
            serializer.serialize(config);
        } catch (ConfigSerializer.SerializationException e) {
            logger.error("Failed to save config '{}'", configClass, e);
        }
    }

    private boolean load() {
        try {
            config = serializer.deserialize();
            config.validatePostLoad();
            return true;
        } catch (ConfigSerializer.SerializationException | ConfigData.ValidationException e) {
            logger.error("Failed to load config '{}', using default!", configClass, e);
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
