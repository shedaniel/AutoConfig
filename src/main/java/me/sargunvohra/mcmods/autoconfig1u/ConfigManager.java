package me.sargunvohra.mcmods.autoconfig1u;

import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.event.ConfigSerializeEvent;
import me.sargunvohra.mcmods.autoconfig1u.serializer.ConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.util.Utils;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class ConfigManager<T extends ConfigData> implements ConfigHolder<T> {
    private final Logger logger;
    private final Config definition;
    private final Class<T> configClass;
    private final ConfigSerializer<T> serializer;
    private final Event<ConfigSerializeEvent.Save<T>> saveEvent = EventFactory.createArrayBacked(ConfigSerializeEvent.Save.class,
        (listeners) -> (config, data) -> {
            for (ConfigSerializeEvent.Save<T> listener : listeners) {
                ActionResult result = listener.onSave(config, data);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }

            return ActionResult.PASS;
        });
    private final Event<ConfigSerializeEvent.Load<T>> loadEvent = EventFactory.createArrayBacked(ConfigSerializeEvent.Load.class,
        (listeners) -> (config, newData) -> {
            for (ConfigSerializeEvent.Load<T> listener : listeners) {
                ActionResult result = listener.onLoad(config, newData);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }

            return ActionResult.PASS;
        });

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

    @Override
    @NotNull
    public Class<T> getConfigClass() {
        return configClass;
    }

    public ConfigSerializer<T> getSerializer() {
        return serializer;
    }

    @Override
    public void save() {
        if (saveEvent.invoker().onSave(this, config) == ActionResult.FAIL) {
            return;
        }
        try {
            serializer.serialize(config);
        } catch (ConfigSerializer.SerializationException e) {
            logger.error("Failed to save config '{}'", configClass, e);
        }
    }

    @Override
    public boolean load() {
        try {
            T deserialized = serializer.deserialize();
                    ActionResult result = loadEvent.invoker().onLoad(this, deserialized);
                    if (result == ActionResult.FAIL) {
                        config = serializer.createDefault();
                        config.validatePostLoad();
                        return false;
                    }
            config = deserialized;
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

    @Override
    public void registerLoadListener(ConfigSerializeEvent.Load<T> load) {
        this.loadEvent.register(load);
    }

    @Override
    public void registerSaveListener(ConfigSerializeEvent.Save<T> save) {
        this.saveEvent.register(save);
    }
}
