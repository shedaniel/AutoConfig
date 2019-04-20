package me.sargunvohra.mcmods.autoconfig.api;

import me.sargunvohra.mcmods.autoconfig.impl.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AutoConfig {
    private static Map<String, ConfigHolder> configHolders = new HashMap<>();

    private AutoConfig() {
    }

    @SuppressWarnings("WeakerAccess")
    public static <T extends ConfigData> ConfigHolder<T> register(
        String configName,
        Class<T> configClass,
        ConfigSerializer.Factory<T> serializerFactory
    ) {
        if (configHolders.containsKey(configName)) {
            throw new RuntimeException(String.format("Config '%s' already registered", configName));
        }

        ConfigSerializer<T> serializer = serializerFactory.create(configName, configClass);
        ConfigManager<T> manager = new ConfigManager<>(configName, configClass, serializer);
        configHolders.put(configName, manager);

        return manager;
    }

    @SuppressWarnings({"unused"})
    public static <T extends ConfigData> ConfigHolder<T> registerGson(
        String configName,
        Class<T> configClass
    ) {
        return register(configName, configClass, GsonConfigSerializer::new);
    }

    @SuppressWarnings({"unused"})
    public static <T extends ConfigData> ConfigHolder<T> registerJankson(
        String configName,
        Class<T> configClass
    ) {
        return register(configName, configClass, JanksonConfigSerializer::new);
    }

    @SuppressWarnings({"unused"})
    public static <T extends ConfigData> ConfigHolder<T> registerToml4j(
        String configName,
        Class<T> configClass
    ) {
        return register(configName, configClass, Toml4jConfigSerializer::new);
    }

    @SuppressWarnings({"unused"})
    public static <T extends ConfigData> ConfigHolder<T> registerDummy(
        String configName,
        Class<T> configClass
    ) {
        return register(configName, configClass, DummyConfigSerializer::new);
    }

    @SuppressWarnings("WeakerAccess")
    public static <T extends ConfigData> ConfigHolder<T> getConfigHolder(String name) {
        if (configHolders.containsKey(name)) {
            //noinspection unchecked
            return (ConfigHolder<T>) configHolders.get(name);
        } else {
            throw new RuntimeException(String.format("Config '%s' has not been registered", name));
        }
    }

    @Environment(EnvType.CLIENT)
    public static <T extends ConfigData> Supplier<Screen> getConfigScreen(String name, Screen parent) {
        //noinspection unchecked
        return new <T>ConfigScreenProvider(((ConfigManager<T>) getConfigHolder(name)), parent);
    }
}
