package me.sargunvohra.mcmods.autoconfig1.gui;

import me.sargunvohra.mcmods.autoconfig1.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1.gui.registry.GuiRegistry;
import me.sargunvohra.mcmods.autoconfig1.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static me.sargunvohra.mcmods.autoconfig1.util.Utils.getUnsafely;
import static me.sargunvohra.mcmods.autoconfig1.util.Utils.setUnsafely;

@Environment(EnvType.CLIENT)
public class DefaultGuiProviders {

    private static final String RESET_KEY = "text.cloth-config.reset_value";

    private DefaultGuiProviders() {
    }

    public static GuiRegistry apply(GuiRegistry registry) {

        registry.registerAnnotationProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.emptyList(),
            ConfigEntry.Gui.Excluded.class
        );

        registry.registerAnnotationProvider(
            (i13n, field, config, defaults, guiProvider) -> {
                ConfigEntry.BoundedDiscrete bounds
                    = field.getAnnotation(ConfigEntry.BoundedDiscrete.class);

                return Collections.singletonList(
                    new IntegerSliderEntry(
                        i13n,
                        (int) bounds.min(),
                        (int) bounds.max(),
                        getUnsafely(field, config, 0),
                        RESET_KEY,
                        () -> getUnsafely(field, defaults),
                        newValue -> setUnsafely(field, config, newValue)
                    )
                );
            },
            field -> field.getType() == int.class || field.getType() == Integer.class,
            ConfigEntry.BoundedDiscrete.class
        );

        registry.registerAnnotationProvider(
            (i13n, field, config, defaults, guiProvider) -> {
                ConfigEntry.BoundedDiscrete bounds
                    = field.getAnnotation(ConfigEntry.BoundedDiscrete.class);

                return Collections.singletonList(
                    new LongSliderEntry(
                        i13n,
                        bounds.min(),
                        bounds.max(),
                        getUnsafely(field, config, 0L),
                        newValue -> setUnsafely(field, config, newValue),
                        RESET_KEY,
                        () -> getUnsafely(field, defaults)
                    )
                );
            },
            field -> field.getType() == long.class || field.getType() == Long.class,
            ConfigEntry.BoundedDiscrete.class
        );

        registry.registerAnnotationProvider(
            DefaultGuiProviders::getChildren,
            field -> !field.getType().isPrimitive(),
            ConfigEntry.Gui.TransitiveObject.class
        );

        registry.registerAnnotationProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new SubCategoryListEntry(
                    i13n,
                    getChildren(i13n, field, config, defaults, guiProvider),
                    field.getAnnotation(ConfigEntry.Gui.CollapsibleObject.class).startExpanded()
                )
            ),
            field -> !field.getType().isPrimitive(),
            ConfigEntry.Gui.CollapsibleObject.class
        );

        //noinspection unchecked
        registry.registerPredicateProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new EnumListEntry(
                    i13n,
                    field.getType(),
                    getUnsafely(field, config),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            field -> field.getType().isEnum()
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new BooleanListEntry(
                    i13n,
                    getUnsafely(field, config, false),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            boolean.class, Boolean.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new IntegerListEntry(
                    i13n,
                    getUnsafely(field, config, 0),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            int.class, Integer.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new LongListEntry(
                    i13n,
                    getUnsafely(field, config, 0L),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            long.class, Long.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new FloatListEntry(
                    i13n,
                    getUnsafely(field, config, 0f),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            float.class, Float.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new DoubleListEntry(
                    i13n,
                    getUnsafely(field, config, 0.0),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            double.class, Double.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new StringListEntry(
                    i13n,
                    getUnsafely(field, config, ""),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            String.class
        );

        return registry;
    }

    private static List<AbstractConfigListEntry> getChildren(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess guiProvider) {
        Object iConfig = getUnsafely(field, config);
        Object iDefaults = getUnsafely(field, defaults);

        return Arrays.stream(field.getType().getDeclaredFields())
            .map(
                iField -> {
                    String iI13n = String.format("%s.%s", i13n, iField.getName());
                    return guiProvider.getAndTransform(iI13n, iField, iConfig, iDefaults, guiProvider);
                }
            )
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }
}
