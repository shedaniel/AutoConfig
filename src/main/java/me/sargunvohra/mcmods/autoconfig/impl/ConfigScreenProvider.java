package me.sargunvohra.mcmods.autoconfig.impl;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry;
import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry.LongSlider;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.cloth.gui.entries.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry.IntSlider;

@Environment(EnvType.CLIENT)
public class ConfigScreenProvider<T extends ConfigData> implements Supplier<Screen> {
    private ConfigManager<T> manager;
    private Screen parent;

    public ConfigScreenProvider(ConfigManager<T> manager, Screen parent) {
        this.manager = manager;
        this.parent = parent;
    }

    private static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
        Function<? super T, ? extends K> keyMapper,
        Function<? super T, ? extends U> valueMapper
    ) {
        return toMap(
            keyMapper,
            valueMapper,
            (u, v) -> {
                throw new IllegalStateException(String.format("Duplicate key %s", u));
            },
            LinkedHashMap::new
        );
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

    private static boolean fieldTypeIsOneOf(Field field, Class... types) {
        return Arrays.stream(types).anyMatch(type -> field.getType() == type);
    }

    @Override
    public Screen get() {
        T current = manager.getConfig();
        T defaults = manager.getSerializer().createDefault();

        String baseI13n = String.format("text.%s.config", manager.getName());
        String resetI13n = "text.cloth.reset_value";

        ClothConfigScreen.Builder builder = new ClothConfigScreen.Builder(
            parent, String.format("%s.title", baseI13n), (savedConfig) -> manager.save());

        Map<Field, ConfigGuiEntry> guiFields =
            Arrays.stream(manager.getConfigClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ConfigGuiEntry.class))
                .collect(
                    toLinkedMap(
                        identity(),
                        field -> field.getAnnotation(ConfigGuiEntry.class)
                    )
                );

        Map<String, ConfigScreenBuilder.CategoryBuilder> categories =
            guiFields.values().stream()
                .map(ConfigGuiEntry::category)
                .distinct()
                .collect(
                    toMap(
                        identity(),
                        name -> builder.addCategory(String.format("%s.category.%s", baseI13n, name))
                    )
                );

        guiFields.forEach((field, entry) -> {
            String category = entry.category();
            String optionI13n = String.format("%s.option.%s.%s", baseI13n, category, field.getName());

            field.setAccessible(true);
            ClothConfigScreen.AbstractListEntry optionGui;

            if (field.isAnnotationPresent(IntSlider.class)) {
                IntSlider slider = field.getAnnotation(IntSlider.class);
                optionGui = new IntegerSliderEntry(
                    optionI13n,
                    slider.min(),
                    slider.max(),
                    getUnsafely(field, current),
                    resetI13n,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, current, newValue)
                );
            } else if (field.isAnnotationPresent(LongSlider.class)) {
                LongSlider slider = field.getAnnotation(LongSlider.class);
                optionGui = new LongSliderEntry(
                    optionI13n,
                    slider.min(),
                    slider.max(),
                    getUnsafely(field, current),
                    newValue -> setUnsafely(field, current, newValue),
                    resetI13n,
                    () -> getUnsafely(field, defaults)
                );
            } else if (field.getType().isEnum()) {
                //noinspection unchecked
                optionGui = new EnumListEntry(
                    optionI13n,
                    field.getType(),
                    getUnsafely(field, current),
                    resetI13n,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, current, newValue)
                );
            } else if (fieldTypeIsOneOf(field, boolean.class, Boolean.class)) {
                optionGui = new BooleanListEntry(
                    optionI13n,
                    getUnsafely(field, current),
                    resetI13n,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, current, newValue)
                );
            } else if (fieldTypeIsOneOf(field, double.class, Double.class)) {
                optionGui = new DoubleListEntry(
                    optionI13n,
                    getUnsafely(field, current),
                    resetI13n,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, current, newValue)
                );
            } else if (fieldTypeIsOneOf(field, float.class, Float.class)) {
                optionGui = new FloatListEntry(
                    optionI13n,
                    getUnsafely(field, current),
                    resetI13n,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, current, newValue)
                );
            } else if (fieldTypeIsOneOf(field, int.class, Integer.class)) {
                optionGui = new IntegerListEntry(
                    optionI13n,
                    getUnsafely(field, current),
                    resetI13n,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, current, newValue)
                );
            } else if (fieldTypeIsOneOf(field, long.class, Long.class)) {
                optionGui = new LongListEntry(
                    optionI13n,
                    getUnsafely(field, current),
                    resetI13n,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, current, newValue)
                );
            } else if (fieldTypeIsOneOf(field, String.class)) {
                optionGui = new StringListEntry(
                    optionI13n,
                    getUnsafely(field, current),
                    resetI13n,
                    () -> getUnsafely(field, defaults),
                    newValue -> setUnsafely(field, current, newValue)
                );
            } else {
                throw new IllegalStateException(
                    String.format("Gui entry is not possible for field '%s'", field));
            }

            categories.get(category).addOption(optionGui);
        });

        return builder.build();
    }
}
