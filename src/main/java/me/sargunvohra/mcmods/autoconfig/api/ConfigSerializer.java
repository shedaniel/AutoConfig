package me.sargunvohra.mcmods.autoconfig.api;

public interface ConfigSerializer<T extends ConfigData> {

    void serialize(T config) throws SerializationException;

    T deserialize() throws SerializationException;

    T createDefault();

    @FunctionalInterface
    interface Factory<T extends ConfigData> {
        ConfigSerializer<T> create(String name, Class<T> configClass);
    }

    class SerializationException extends Exception {
        public SerializationException(Throwable cause) {
            super(cause);
        }
    }
}

