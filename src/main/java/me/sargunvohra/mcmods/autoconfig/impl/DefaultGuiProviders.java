package me.sargunvohra.mcmods.autoconfig.impl;

import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiRegistry;
import me.shedaniel.cloth.gui.entries.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import static me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry.*;
import static me.sargunvohra.mcmods.autoconfig.impl.Utils.getUnsafely;
import static me.sargunvohra.mcmods.autoconfig.impl.Utils.setUnsafely;

@Environment(EnvType.CLIENT)
public class DefaultGuiProviders {

    private static final String RESET_KEY = "text.cloth.reset_value";

    private DefaultGuiProviders() {
    }

    public static ConfigGuiRegistry apply(ConfigGuiRegistry registry) {

        registry.registerForAnnotations(
            (i13n, field, config, defaults, guiProvider) -> {
                IntSlider slider = field.getAnnotation(IntSlider.class);
                return Collections.singletonList(
                    new IntegerSliderEntry(
                        i13n,
                        slider.min(),
                        slider.max(),
                        getUnsafely(field, config),
                        RESET_KEY,
                        () -> getUnsafely(field, defaults),
                        newValue -> setUnsafely(field, config, newValue)
                    )
                );
            },
            IntSlider.class
        );

        registry.registerForAnnotations(
            (i13n, field, config, defaults, guiProvider) -> {
                LongSlider slider = field.getAnnotation(LongSlider.class);
                return Collections.singletonList(
                    new LongSliderEntry(
                        i13n,
                        slider.min(),
                        slider.max(),
                        getUnsafely(field, config),
                        newValue -> setUnsafely(field, config, newValue),
                        RESET_KEY,
                        () -> getUnsafely(field, defaults)
                    )
                );
            },
            LongSlider.class
        );

        registry.registerForAnnotations(
            (i13n, field, config, defaults, guiProvider) -> {
                Object iConfig = getUnsafely(field, config);
                Object iDefaults = getUnsafely(field, defaults);

                return Arrays.stream(field.getType().getDeclaredFields())
                    .map(
                        iField -> {
                            String iI13n = String.format("%s.%s", i13n, iField.getName());
                            return guiProvider.get(iI13n, iField, iConfig, iDefaults, guiProvider);
                        }
                    )
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            },
            Transitive.class
        );

        //noinspection unchecked
        registry.registerForPredicate(
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

        registry.registerForTypes(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new BooleanListEntry(
                    i13n,
                    getUnsafely(field, config),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            boolean.class, Boolean.class
        );

        registry.registerForTypes(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new IntegerListEntry(
                    i13n,
                    getUnsafely(field, config),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            int.class, Integer.class
        );

        registry.registerForTypes(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new LongListEntry(
                    i13n,
                    getUnsafely(field, config),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            long.class, Long.class
        );

        registry.registerForTypes(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new FloatListEntry(
                    i13n,
                    getUnsafely(field, config),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            float.class, Float.class
        );

        registry.registerForTypes(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new DoubleListEntry(
                    i13n,
                    getUnsafely(field, config),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            double.class, Double.class
        );

        registry.registerForTypes(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                new StringListEntry(
                    i13n,
                    getUnsafely(field, config),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, config, newValue)
                )
            ),
            String.class
        );

        return registry;
    }
}
