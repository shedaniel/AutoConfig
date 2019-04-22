package me.sargunvohra.mcmods.autoconfig1;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public final class ConfigGuiRegistry implements ConfigGuiProvider {

    private Map<Priority, List<RegistryEntry>> providers = new HashMap<>();

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
        ConfigGuiProvider guiProvider
    ) {
        return firstPresent(
            Arrays.stream(Priority.values())
                .map(priority ->
                    (Supplier<Optional<RegistryEntry>>) () ->
                        providers.get(priority).stream()
                            .filter(entry -> entry.predicate.test(field))
                            .findFirst()
                )
        )
            .map(entry -> entry.provider.get(i13n, field, config, defaults, guiProvider))
            .orElse(null);
    }

    private void register(Priority priority, ConfigGuiProvider provider, Predicate<Field> predicate) {
        providers.computeIfAbsent(priority, p -> new ArrayList<>()).add(new RegistryEntry(predicate, provider));
    }

    @SuppressWarnings("WeakerAccess")
    public final void registerForTypes(ConfigGuiProvider provider, Class... types) {
        for (Class type : types) {
            register(Priority.LAST, provider, field -> type == field.getType());
        }
    }

    @SuppressWarnings("WeakerAccess")
    public final void registerForPredicate(ConfigGuiProvider provider, Predicate<Field> predicate) {
        register(Priority.NORMAL, provider, predicate);
    }

    @SuppressWarnings("WeakerAccess")
    @SafeVarargs
    public final void registerForAnnotations(ConfigGuiProvider provider, Class<? extends Annotation>... types) {
        for (Class<? extends Annotation> type : types) {
            register(Priority.FIRST, provider, field -> field.isAnnotationPresent(type));
        }
    }

    @SuppressWarnings("WeakerAccess")
    @SafeVarargs
    public final void registerForAnnotations(ConfigGuiProvider provider, Predicate<Field> predicate, Class<? extends Annotation>... types) {
        for (Class<? extends Annotation> type : types) {
            register(
                Priority.FIRST,
                provider,
                field -> predicate.test(field) && field.isAnnotationPresent(type)
            );
        }
    }

    private enum Priority {
        FIRST, NORMAL, LAST
    }

    private static class RegistryEntry {
        final Predicate<Field> predicate;
        final ConfigGuiProvider provider;

        RegistryEntry(Predicate<Field> predicate, ConfigGuiProvider provider) {
            this.predicate = predicate;
            this.provider = provider;
        }
    }
}
