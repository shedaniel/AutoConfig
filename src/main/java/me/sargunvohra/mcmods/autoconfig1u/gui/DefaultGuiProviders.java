package me.sargunvohra.mcmods.autoconfig1u.gui;

import com.google.common.collect.Lists;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.GuiRegistry;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.getUnsafely;
import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.setUnsafely;

@Environment(EnvType.CLIENT)
public class DefaultGuiProviders {

    private static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();
    private static final Function<Enum, Text> DEFAULT_NAME_PROVIDER = t -> new TranslatableText(t instanceof SelectionListEntry.Translatable ? ((SelectionListEntry.Translatable) t).getKey() : t.toString());

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
                    ENTRY_BUILDER.startIntSlider(
                        new TranslatableText(i13n),
                        getUnsafely(field, config, 0),
                        (int) bounds.min(),
                        (int) bounds.max()
                    )
                        .setDefaultValue(() -> getUnsafely(field, defaults))
                        .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                        .build()
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
                    ENTRY_BUILDER.startLongSlider(
                        new TranslatableText(i13n),
                        getUnsafely(field, config, 0L),
                        bounds.min(),
                        bounds.max()
                    )
                        .setDefaultValue(() -> getUnsafely(field, defaults))
                        .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                        .build()
                );
            },
            field -> field.getType() == long.class || field.getType() == Long.class,
            ConfigEntry.BoundedDiscrete.class
        );

        registry.registerAnnotationProvider(
            (i13n, field, config, defaults, guiProvider) -> {
                ConfigEntry.ColorPicker colorPicker
                    = field.getAnnotation(ConfigEntry.ColorPicker.class);

                return Collections.singletonList(
                    ENTRY_BUILDER.startColorField(
                        new TranslatableText(i13n),
                        getUnsafely(field, config, 0)
                    )
                        .setAlphaMode(colorPicker.allowAlpha())
                        .setDefaultValue(() -> getUnsafely(field, defaults))
                        .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                        .build()
                );
            },
            field -> field.getType() == int.class || field.getType() == Integer.class,
            ConfigEntry.ColorPicker.class
        );

        registry.registerAnnotationProvider(
            DefaultGuiProviders::getChildren,
            field -> !field.getType().isPrimitive(),
            ConfigEntry.Gui.TransitiveObject.class
        );

        registry.registerAnnotationProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startSubCategory(
                    new TranslatableText(i13n),
                    getChildren(i13n, field, config, defaults, guiProvider)
                )
                    .setExpanded(field.getAnnotation(ConfigEntry.Gui.CollapsibleObject.class).startExpanded())
                    .build()
            ),
            field -> !field.getType().isPrimitive(),
            ConfigEntry.Gui.CollapsibleObject.class
        );

        registry.registerPredicateProvider(
            (i13n, field, config, defaults, guiProvider) -> {
                Object[] enumConstants = field.getType().getEnumConstants();
                Enum[] enums = new Enum[enumConstants.length];
                for (int i = 0; i < enumConstants.length; i++) {
                    enums[i] = (Enum) enumConstants[i];
                }
                return Collections.singletonList(
                    ENTRY_BUILDER.startSelector(
                        new TranslatableText(i13n),
                        enums,
                        getUnsafely(field, config, null)
                    )
                        .setDefaultValue(() -> getUnsafely(field, defaults))
                        .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                        .build()
                );
            },
            field -> field.getType().isEnum() && field.isAnnotationPresent(ConfigEntry.Gui.EnumHandler.class) && field.getAnnotation(ConfigEntry.Gui.EnumHandler.class).option() == ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON
        );

        //noinspection unchecked
        registry.registerPredicateProvider(
            (i13n, field, config, defaults, guiProvider) -> {
                List<Enum> enums = new ArrayList<>();
                for (Object constant : field.getType().getEnumConstants()) {
                    enums.add((Enum) constant);
                }
                return Collections.singletonList(
                    ENTRY_BUILDER.startDropdownMenu(
                        new TranslatableText(i13n),
                        DropdownMenuBuilder.TopCellElementBuilder.of(
                            getUnsafely(field, config, null),
                            str -> {
                                for (Object constant : field.getType().getEnumConstants()) {
                                    if (DEFAULT_NAME_PROVIDER.apply((Enum) constant).equals(str)) {
                                        return (Enum) constant;
                                    }
                                }
                                return null;
                            },
                            DEFAULT_NAME_PROVIDER
                        ),
                        DropdownMenuBuilder.CellCreatorBuilder.of(DEFAULT_NAME_PROVIDER)
                    )
                        .setSelections(enums)
                        .setDefaultValue(() -> getUnsafely(field, defaults))
                        .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                        .build()
                );
            },
            field -> field.getType().isEnum()
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startBooleanToggle(
                    new TranslatableText(i13n),
                    getUnsafely(field, config, false)
                )
                    .setDefaultValue(() -> getUnsafely(field, defaults))
                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                    .setYesNoTextSupplier(bool -> {
                        String key = i13n + ".boolean." + bool;
                        String translate = I18n.translate(key);
                        if (translate.equals(key))
                            return new TranslatableText("text.cloth-config.boolean.value." + bool);
                        return new LiteralText(translate);
                    })
                    .build()
            ),
            boolean.class, Boolean.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startIntField(
                    new TranslatableText(i13n),
                    getUnsafely(field, config, 0)
                )
                    .setDefaultValue(() -> getUnsafely(field, defaults))
                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                    .build()
            ),
            int.class, Integer.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startIntList(
                    new TranslatableText(i13n),
                    Lists.newArrayList(getUnsafely(field, config, new Integer[0]))
                )
                    .setDefaultValue(() -> getUnsafely(field, defaults))
                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue.toArray(new Integer[0])))
                    .build()
            ),
            Integer[].class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startLongField(
                    new TranslatableText(i13n),
                    getUnsafely(field, config, 0L)
                )
                    .setDefaultValue(() -> getUnsafely(field, defaults))
                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                    .build()
            ),
            long.class, Long.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startFloatField(
                    new TranslatableText(i13n),
                    getUnsafely(field, config, 0f)
                )
                    .setDefaultValue(() -> getUnsafely(field, defaults))
                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                    .build()
            ),
            float.class, Float.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startDoubleField(
                    new TranslatableText(i13n),
                    getUnsafely(field, config, 0.0)
                )
                    .setDefaultValue(() -> getUnsafely(field, defaults))
                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                    .build()
            ),
            double.class, Double.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startStrField(
                    new TranslatableText(i13n),
                    getUnsafely(field, config, "")
                )
                    .setDefaultValue(() -> getUnsafely(field, defaults))
                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                    .build()
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
