package me.sargunvohra.mcmods.autoconfig1u.serializer;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.util.Utils;

/**
 * This serializer doesn't serialize anything. Why would you ever use this?
 */
public class DummyConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
    
    private final Class<T> configClass;
    
    public DummyConfigSerializer(@SuppressWarnings("unused") Config definition, Class<T> configClass) {
        this.configClass = configClass;
    }
    
    @Override
    public void serialize(T config) {
    }
    
    @Override
    public T deserialize() {
        return createDefault();
    }
    
    @Override
    public T createDefault() {
        return Utils.constructUnsafely(configClass);
    }
}
