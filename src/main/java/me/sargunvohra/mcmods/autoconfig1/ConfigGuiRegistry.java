package me.sargunvohra.mcmods.autoconfig1;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public final class ConfigGuiRegistry implements ConfigGuiProviderTransformer {

    private Map<Priority, List<ProviderEntry>> providers = new HashMap<>();
    private List<TransformerEntry> transformers = new ArrayList<>();

    ConfigGuiRegistry() {
        for (Priority priority : Priority.values()) {
            providers.put(priority, new ArrayList<>());
        }
    }

    private static <T> Optional<T> firstPresent(Stream<Supplier<Optional<T>>> optionals) {
        return optionals
            .map(Supplier::get)
            .filter(Optional::isPresent)
            .findFirst()
            .orElse(Optional.empty());
    }

    @Override
    public List<ClothConfigScreen.AbstractListEntry> get(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        ConfigGuiProviderTransformer registry
    ) {
        return firstPresent(
            Arrays.stream(Priority.values())
                .map(priority ->
                    (Supplier<Optional<ProviderEntry>>) () ->
                        providers.get(priority).stream()
                            .filter(entry -> entry.predicate.test(field))
                            .findFirst()
                )
        )
            .map(entry -> entry.provider.get(i13n, field, config, defaults, registry))
            .orElse(null);
    }

    @Override
    public List<ClothConfigScreen.AbstractListEntry> transform(
        List<ClothConfigScreen.AbstractListEntry> guis,
        String i13n,
        Field field,
        Object config,
        Object defaults,
        ConfigGuiProviderTransformer registry
    ) {
        List<ConfigGuiTransformer> matchedTransformers = this.transformers.stream()
            .filter(entry -> entry.predicate.test(field))
            .map(entry -> entry.transformer)
            .collect(Collectors.toList());

        for (ConfigGuiTransformer transformer : matchedTransformers) {
            guis = transformer.transform(guis, i13n, field, config, defaults, registry);
        }

        return guis;
    }

    private void registerProvider(Priority priority, ConfigGuiProvider provider, Predicate<Field> predicate) {
        providers.computeIfAbsent(priority, p -> new ArrayList<>()).add(new ProviderEntry(predicate, provider));
    }

    @SuppressWarnings("WeakerAccess")
    public final void registerTypeProvider(ConfigGuiProvider provider, Class... types) {
        for (Class type : types) {
            registerProvider(Priority.LAST, provider, field -> type == field.getType());
        }
    }

    @SuppressWarnings("WeakerAccess")
    public final void registerPredicateProvider(ConfigGuiProvider provider, Predicate<Field> predicate) {
        registerProvider(Priority.NORMAL, provider, predicate);
    }

    @SuppressWarnings("WeakerAccess")
    @SafeVarargs
    public final void registerAnnotationProvider(ConfigGuiProvider provider, Class<? extends Annotation>... types) {
        for (Class<? extends Annotation> type : types) {
            registerProvider(Priority.FIRST, provider, field -> field.isAnnotationPresent(type));
        }
    }

    @SuppressWarnings("WeakerAccess")
    @SafeVarargs
    public final void registerAnnotationProvider(ConfigGuiProvider provider, Predicate<Field> predicate, Class<? extends Annotation>... types) {
        for (Class<? extends Annotation> type : types) {
            registerProvider(
                Priority.FIRST,
                provider,
                field -> predicate.test(field) && field.isAnnotationPresent(type)
            );
        }
    }

    public void registerPredicateTransformer(ConfigGuiTransformer transformer, Predicate<Field> predicate) {
        transformers.add(new TransformerEntry(predicate, transformer));
    }

    @SafeVarargs
    public final void registerAnnotationTransformer(ConfigGuiTransformer transformer, Class<? extends Annotation>... types) {
        registerAnnotationTransformer(transformer, field -> true, types);
    }

    @SafeVarargs
    public final void registerAnnotationTransformer(ConfigGuiTransformer transformer, Predicate<Field> predicate, Class<? extends Annotation>... types) {
        for (Class<? extends Annotation> type : types) {
            registerPredicateTransformer(transformer, field -> predicate.test(field) && field.isAnnotationPresent(type));
        }
    }

    private enum Priority {
        FIRST, NORMAL, LAST
    }

    private static class ProviderEntry {
        final Predicate<Field> predicate;
        final ConfigGuiProvider provider;

        ProviderEntry(Predicate<Field> predicate, ConfigGuiProvider provider) {
            this.predicate = predicate;
            this.provider = provider;
        }
    }

    private static class TransformerEntry {
        final Predicate<Field> predicate;
        final ConfigGuiTransformer transformer;

        TransformerEntry(Predicate<Field> predicate, ConfigGuiTransformer transformer) {
            this.predicate = predicate;
            this.transformer = transformer;
        }
    }
}
