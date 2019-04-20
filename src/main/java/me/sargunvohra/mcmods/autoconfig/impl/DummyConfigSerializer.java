package me.sargunvohra.mcmods.autoconfig.impl;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigSerializer;

import java.lang.reflect.Constructor;

public class DummyConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {

    private final Class<T> configClass;

    public DummyConfigSerializer(String name, Class<T> configClass) {
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
        try {
            Constructor<T> constructor = configClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
