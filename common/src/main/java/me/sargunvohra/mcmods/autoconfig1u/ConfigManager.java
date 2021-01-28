package me.sargunvohra.mcmods.autoconfig1u;

import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.event.ConfigSerializeEvent;
import me.sargunvohra.mcmods.autoconfig1u.serializer.ConfigSerializer;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import net.minecraft.world.InteractionResult;
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
    
    private final Event<ConfigSerializeEvent.Save<T>> saveEvent = EventFactory.createInteractionResult();
    private final Event<ConfigSerializeEvent.Load<T>> loadEvent = EventFactory.createInteractionResult();
    
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
        if (saveEvent.invoker().onSave(this, config) == InteractionResult.FAIL) {
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
            InteractionResult result = loadEvent.invoker().onLoad(this, deserialized);
            if (result == InteractionResult.FAIL) {
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
