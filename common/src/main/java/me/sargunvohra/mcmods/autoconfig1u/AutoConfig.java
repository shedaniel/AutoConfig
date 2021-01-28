package me.sargunvohra.mcmods.autoconfig1u;

import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.gui.ConfigScreenProvider;
import me.sargunvohra.mcmods.autoconfig1u.gui.DefaultGuiProviders;
import me.sargunvohra.mcmods.autoconfig1u.gui.DefaultGuiTransformers;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.ComposedGuiRegistryAccess;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.DefaultGuiRegistryAccess;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.GuiRegistry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.ConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class AutoConfig {
    public static final String MOD_ID = "autoconfig1u";
    
    private static final Map<Class<? extends ConfigData>, ConfigHolder<?>> holders = new HashMap<>();
    private static final Map<Class<? extends ConfigData>, GuiRegistry> guiRegistries = new HashMap<>();
    
    private AutoConfig() {
    }
    
    public static <T extends ConfigData> ConfigHolder<T> register(
            Class<T> configClass,
            ConfigSerializer.Factory<T> serializerFactory
    ) {
        Objects.requireNonNull(configClass);
        Objects.requireNonNull(serializerFactory);
        
        if (holders.containsKey(configClass)) {
            throw new RuntimeException(String.format("Config '%s' already registered", configClass));
        }
        
        Config definition = configClass.getAnnotation(Config.class);
        
        if (definition == null) {
            throw new RuntimeException(String.format("No @Config annotation on %s!", configClass));
        }
        
        ConfigSerializer<T> serializer = serializerFactory.create(definition, configClass);
        ConfigManager<T> manager = new ConfigManager<>(definition, configClass, serializer);
        holders.put(configClass, manager);
        
        return manager;
    }
    
    public static <T extends ConfigData> ConfigHolder<T> getConfigHolder(Class<T> configClass) {
        Objects.requireNonNull(configClass);
        if (holders.containsKey(configClass)) {
            return (ConfigHolder<T>) holders.get(configClass);
        } else {
            throw new RuntimeException(String.format("Config '%s' has not been registered", configClass));
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static <T extends ConfigData> GuiRegistry getGuiRegistry(Class<T> configClass) {
        return guiRegistries.computeIfAbsent(configClass, n -> new GuiRegistry());
    }
    
    @Environment(EnvType.CLIENT)
    public static <T extends ConfigData> Supplier<Screen> getConfigScreen(Class<T> configClass, Screen parent) {
        return new ConfigScreenProvider<>(
                (ConfigManager<T>) AutoConfig.getConfigHolder(configClass),
                new ComposedGuiRegistryAccess(
                        getGuiRegistry(configClass),
                        ClientOnly.defaultGuiRegistry,
                        new DefaultGuiRegistryAccess()
                ),
                parent
        );
    }
    
    @Environment(EnvType.CLIENT)
    private static class ClientOnly {
        private static final GuiRegistry defaultGuiRegistry =
                DefaultGuiTransformers.apply(DefaultGuiProviders.apply(new GuiRegistry()));
    }
}
