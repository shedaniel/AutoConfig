package me.sargunvohra.mcmods.autoconfig.api;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public final class ConfigGuiRegistry implements ConfigGuiProvider {
    private Map<Class, ConfigGuiProvider> typeProviders = new LinkedHashMap<>();
    private Map<Predicate<Field>, ConfigGuiProvider> predicateProviders = new LinkedHashMap<>();
    private Map<Class<? extends Annotation>, ConfigGuiProvider> annotationProviders = new LinkedHashMap<>();

    private static <K, V> void registerUniquely(Map<K, V> registry, K key, V value) {
        if (registry.containsKey(key))
            throw new IllegalArgumentException(String.format("Key %s registered twice!", key));
        registry.put(key, value);
    }

    @SafeVarargs
    private static <T> Optional<T> firstPresent(final Supplier<Optional<T>>... optionals) {
        return Stream.of(optionals)
            .map(Supplier::get)
            .filter(Optional::isPresent)
            .findFirst()
            .orElse(Optional.empty());
    }

    @Override
    public final List<ClothConfigScreen.AbstractListEntry> get(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        ConfigGuiProvider guiProvider) {
        return firstPresent(
            () -> annotationProviders.entrySet().stream()
                .filter(entry -> field.isAnnotationPresent(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue),
            () -> predicateProviders.entrySet().stream()
                .filter(entry -> entry.getKey().test(field))
                .findFirst()
                .map(Map.Entry::getValue),
            () -> Optional.ofNullable(typeProviders.get(field.getType()))
        )
            .map(provider -> provider.get(i13n, field, config, defaults, guiProvider))
            .orElse(null);
    }

    public final void registerForTypes(ConfigGuiProvider provider, Class... types) {
        for (Class type : types) {
            registerUniquely(typeProviders, type, provider);
        }
    }

    public final void registerForPredicate(ConfigGuiProvider provider, Predicate<Field> predicate) {
        registerUniquely(predicateProviders, predicate, provider);
    }

    @SafeVarargs
    public final void registerForAnnotations(ConfigGuiProvider provider, Class<? extends Annotation>... types) {
        for (Class<? extends Annotation> type : types) {
            registerUniquely(annotationProviders, type, provider);
        }
    }
}
