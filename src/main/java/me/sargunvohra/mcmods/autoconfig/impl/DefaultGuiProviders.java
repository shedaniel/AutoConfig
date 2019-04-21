package me.sargunvohra.mcmods.autoconfig.impl;

import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiRegistry;
import me.shedaniel.cloth.gui.entries.*;

import java.lang.reflect.Field;

import static me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry.IntSlider;
import static me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry.LongSlider;

public class DefaultGuiProviders {

    private static final String RESET_KEY = "text.cloth.reset_value";

    private DefaultGuiProviders() {
    }

    private static <V> V getUnsafely(Field field, Object obj) {
        try {
            //noinspection unchecked
            return (V) field.get(obj);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setUnsafely(Field field, Object obj, Object newValue) {
        try {
            field.set(obj, newValue);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void apply(ConfigGuiRegistry registry) {

        registry.registerForAnnotations(
            (i13n, field, current, defaults) -> {
                IntSlider slider = field.getAnnotation(IntSlider.class);
                return new IntegerSliderEntry(
                    i13n,
                    slider.min(),
                    slider.max(),
                    getUnsafely(field, current),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, current, newValue)
                );
            },
            IntSlider.class
        );

        registry.registerForAnnotations(
            (i13n, field, current, defaults) -> {
                LongSlider slider = field.getAnnotation(LongSlider.class);
                return new LongSliderEntry(
                    i13n,
                    slider.min(),
                    slider.max(),
                    getUnsafely(field, current),
                    newValue -> setUnsafely(field, current, newValue),
                    RESET_KEY,
                    () -> getUnsafely(field, defaults)
                );
            },
            LongSlider.class
        );

        //noinspection unchecked
        registry.registerForPredicate(
            (i13n, field, config, defaults) -> new EnumListEntry(
                i13n,
                field.getType(),
                getUnsafely(field, config),
                RESET_KEY,
                () -> getUnsafely(field, defaults),
                newValue -> setUnsafely(field, config, newValue)
            ),
            field -> field.getType().isEnum()
        );

        registry.registerForTypes(
            (i13n, field, current, defaults) -> new BooleanListEntry(
                i13n,
                getUnsafely(field, current),
                RESET_KEY,
                () -> getUnsafely(field, defaults),
                newValue -> setUnsafely(field, current, newValue)
            ),
            boolean.class, Boolean.class
        );

        registry.registerForTypes(
            (i13n, field, current, defaults) -> new IntegerListEntry(
                i13n,
                getUnsafely(field, current),
                RESET_KEY,
                () -> getUnsafely(field, defaults),
                newValue -> setUnsafely(field, current, newValue)
            ),
            int.class, Integer.class
        );

        registry.registerForTypes(
            (i13n, field, current, defaults) -> new LongListEntry(
                i13n,
                getUnsafely(field, current),
                RESET_KEY,
                () -> getUnsafely(field, defaults),
                newValue -> setUnsafely(field, current, newValue)
            ),
            long.class, Long.class
        );

        registry.registerForTypes(
            (i13n, field, current, defaults) -> new FloatListEntry(
                i13n,
                getUnsafely(field, current),
                RESET_KEY,
                () -> getUnsafely(field, defaults),
                newValue -> setUnsafely(field, current, newValue)
            ),
            float.class, Float.class
        );

        registry.registerForTypes(
            (i13n, field, current, defaults) -> new DoubleListEntry(
                i13n,
                getUnsafely(field, current),
                RESET_KEY,
                () -> getUnsafely(field, defaults),
                newValue -> setUnsafely(field, current, newValue)
            ),
            double.class, Double.class
        );

        registry.registerForTypes(
            (i13n, field, current, defaults) -> new StringListEntry(
                i13n,
                getUnsafely(field, current),
                RESET_KEY,
                () -> getUnsafely(field, defaults),
                newValue -> setUnsafely(field, current, newValue)
            ),
            String.class
        );
    }
}
