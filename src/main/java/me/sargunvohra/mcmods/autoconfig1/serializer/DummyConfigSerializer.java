package me.sargunvohra.mcmods.autoconfig1.serializer;

import me.sargunvohra.mcmods.autoconfig1.ConfigData;
import me.sargunvohra.mcmods.autoconfig1.ConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1.util.Utils;

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
