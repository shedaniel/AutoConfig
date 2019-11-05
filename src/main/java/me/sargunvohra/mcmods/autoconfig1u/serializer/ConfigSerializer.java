package me.sargunvohra.mcmods.autoconfig1u.serializer;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

public interface ConfigSerializer<T extends ConfigData> {

    void serialize(T config) throws SerializationException;

    T deserialize() throws SerializationException;

    T createDefault();

    @FunctionalInterface
    interface Factory<T extends ConfigData> {
        ConfigSerializer<T> create(Config definition, Class<T> configClass);
    }

    class SerializationException extends Exception {
        public SerializationException(Throwable cause) {
            super(cause);
        }
    }
}

