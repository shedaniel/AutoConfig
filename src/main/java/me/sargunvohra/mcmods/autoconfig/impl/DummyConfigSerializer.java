package me.sargunvohra.mcmods.autoconfig.impl;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigSerializer;

public class DummyConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {

    private final T defaultValue;

    public DummyConfigSerializer(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void serialize(T config) throws SerializationException {
    }

    @Override
    public T deserialize() throws SerializationException {
        return defaultValue;
    }

    @Override
    public T createDefault() {
        return defaultValue;
    }
}
