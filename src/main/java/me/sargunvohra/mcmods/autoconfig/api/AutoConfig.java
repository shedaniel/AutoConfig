package me.sargunvohra.mcmods.autoconfig.api;

import me.sargunvohra.mcmods.autoconfig.api.serializer.DummyConfigSerializer;
import me.sargunvohra.mcmods.autoconfig.api.serializer.GsonConfigSerializer;
import me.sargunvohra.mcmods.autoconfig.api.serializer.JanksonConfigSerializer;
import me.sargunvohra.mcmods.autoconfig.api.serializer.Toml4jConfigSerializer;
import me.sargunvohra.mcmods.autoconfig.impl.ComposedGuiProvider;
import me.sargunvohra.mcmods.autoconfig.impl.ConfigManager;
import me.sargunvohra.mcmods.autoconfig.impl.ConfigScreenProvider;
import me.sargunvohra.mcmods.autoconfig.impl.DefaultGuiProviders;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class AutoConfig {
    private static final Map<String, ConfigHolder> holders = new HashMap<>();
    private static final Map<String, ConfigGuiRegistry> guiRegistries = new HashMap<>();

    private AutoConfig() {
    }

    public static <T extends ConfigData> ConfigHolder<T> register(
        String configName,
        Class<T> configClass,
        ConfigSerializer.Factory<T> serializerFactory
    ) {
        Objects.requireNonNull(configName);
        Objects.requireNonNull(configClass);
        Objects.requireNonNull(serializerFactory);

        if (holders.containsKey(configName)) {
            throw new RuntimeException(String.format("Config '%s' already registered", configName));
        }

        ConfigSerializer<T> serializer = serializerFactory.create(configName, configClass);
        ConfigManager<T> manager = new ConfigManager<>(configName, configClass, serializer);
        holders.put(configName, manager);

        return manager;
    }

    public static <T extends ConfigData> ConfigHolder<T> getConfigHolder(String name) {
        Objects.requireNonNull(name);
        if (holders.containsKey(name)) {
            //noinspection unchecked
            return (ConfigHolder<T>) holders.get(name);
        } else {
            throw new RuntimeException(String.format("Config '%s' has not been registered", name));
        }
    }

    @Environment(EnvType.CLIENT)
    public static ConfigGuiRegistry getGuiRegistry(String name) {
        return guiRegistries.computeIfAbsent(name, n -> new ConfigGuiRegistry());
    }

    @Environment(EnvType.CLIENT)
    public static <T extends ConfigData> Supplier<Screen> getConfigScreen(String name, Screen parent) {
        //noinspection unchecked
        return new <T>ConfigScreenProvider(
            (ConfigManager<T>) AutoConfig.<T>getConfigHolder(name),
            new ComposedGuiProvider(getGuiRegistry(name), ClientOnly.defaultGuiRegistry),
            parent
        );
    }

    @Deprecated
    public static <T extends ConfigData> ConfigHolder<T> registerGson(
        String configName,
        Class<T> configClass
    ) {
        return register(configName, configClass, GsonConfigSerializer::new);
    }

    @Deprecated
    public static <T extends ConfigData> ConfigHolder<T> registerJankson(
        String configName,
        Class<T> configClass
    ) {
        return register(configName, configClass, JanksonConfigSerializer::new);
    }

    @Deprecated
    public static <T extends ConfigData> ConfigHolder<T> registerToml4j(
        String configName,
        Class<T> configClass
    ) {
        return register(configName, configClass, Toml4jConfigSerializer::new);
    }

    @Deprecated
    public static <T extends ConfigData> ConfigHolder<T> registerDummy(
        String configName,
        Class<T> configClass
    ) {
        return register(configName, configClass, DummyConfigSerializer::new);
    }

    @Environment(EnvType.CLIENT)
    private static class ClientOnly {
        private static final ConfigGuiRegistry defaultGuiRegistry
            = DefaultGuiProviders.apply(new ConfigGuiRegistry());
    }
}
