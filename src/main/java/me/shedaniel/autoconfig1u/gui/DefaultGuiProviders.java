package me.shedaniel.autoconfig1u.gui;

import com.google.common.collect.Lists;
import me.shedaniel.autoconfig1u.annotation.ConfigEntry;
import me.shedaniel.autoconfig1u.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig1u.util.Utils;
import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.forge.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.forge.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.forge.gui.entries.NestedListListEntry;
import me.shedaniel.clothconfig2.forge.gui.entries.SelectionListEntry;
import me.shedaniel.clothconfig2.forge.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.shedaniel.autoconfig1u.util.Utils.getUnsafely;
import static me.shedaniel.autoconfig1u.util.Utils.setUnsafely;

@OnlyIn(Dist.CLIENT)
public class DefaultGuiProviders {

    private static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();
    private static final Function<Enum<?>, ITextComponent> DEFAULT_NAME_PROVIDER = t -> new TranslationTextComponent(t instanceof SelectionListEntry.Translatable ? ((SelectionListEntry.Translatable) t).getKey() : t.toString());

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
                        new TranslationTextComponent(i13n),
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
                        new TranslationTextComponent(i13n),
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
                        new TranslationTextComponent(i13n),
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
                    new TranslationTextComponent(i13n),
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
                        new TranslationTextComponent(i13n),
                        enums,
                        getUnsafely(field, config, getUnsafely(field, defaults))
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
                List<Enum<?>> enums = Arrays.asList(((Class<? extends Enum<?>>) field.getType()).getEnumConstants());
                return Collections.singletonList(
                    ENTRY_BUILDER.startDropdownMenu(
                        new TranslationTextComponent(i13n),
                        DropdownMenuBuilder.TopCellElementBuilder.of(
                            getUnsafely(field, config, getUnsafely(field, defaults)),
                            str -> {
                                String s = new StringTextComponent(str).getString();
                                for (Enum<?> constant : enums) {
                                    if (DEFAULT_NAME_PROVIDER.apply(constant).getString().equals(s)) {
                                        return constant;
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

        registry.registerPredicateProvider((i13n, field, config, defaults, registry1) -> Collections.singletonList(
            ENTRY_BUILDER.startIntList(new TranslationTextComponent(i13n), getUnsafely(field, config))
                .setDefaultValue(() -> getUnsafely(field, defaults))
                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                .build()
        ), isListOfType(Integer.class));

        registry.registerPredicateProvider((i13n, field, config, defaults, registry1) -> Collections.singletonList(
            ENTRY_BUILDER.startLongList(new TranslationTextComponent(i13n), getUnsafely(field, config))
                .setDefaultValue(() -> getUnsafely(field, defaults))
                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                .build()
        ), isListOfType(Long.class));

        registry.registerPredicateProvider((i13n, field, config, defaults, registry1) -> Collections.singletonList(
            ENTRY_BUILDER.startFloatList(new TranslationTextComponent(i13n), getUnsafely(field, config))
                .setDefaultValue(() -> getUnsafely(field, defaults))
                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                .build()
        ), isListOfType(Float.class));

        registry.registerPredicateProvider((i13n, field, config, defaults, registry1) -> Collections.singletonList(
            ENTRY_BUILDER.startDoubleList(new TranslationTextComponent(i13n), getUnsafely(field, config))
                .setDefaultValue(() -> getUnsafely(field, defaults))
                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                .build()
        ), isListOfType(Double.class));

        registry.registerPredicateProvider((i13n, field, config, defaults, registry1) -> Collections.singletonList(
            ENTRY_BUILDER.startStrList(new TranslationTextComponent(i13n), getUnsafely(field, config))
                .setDefaultValue(() -> getUnsafely(field, defaults))
                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                .build()
        ), isListOfType(String.class));

        registry.registerPredicateProvider((i13n, field, config, defaults, registry1) -> {
            List<Object> configValue = getUnsafely(field, config);

            Class<?> fieldTypeParam = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

            Object defaultElemValue = Utils.constructUnsafely(fieldTypeParam);

            String remainingI13n = i13n.substring(0, i13n.indexOf(".option") + ".option".length());
            String classI13n = String.format("%s.%s", remainingI13n, fieldTypeParam.getSimpleName());

            return Collections.singletonList(
                new NestedListListEntry<Object, MultiElementListEntry<Object>>(
                    new TranslationTextComponent(i13n),
                    configValue,
                    false,
                    null,
                    abstractConfigListEntries -> {
                    },
                    () -> getUnsafely(field, defaults),
                    ENTRY_BUILDER.getResetButtonKey(),
                    true,
                    true,
                    (elem, nestedListListEntry) -> {
                        if (elem == null) {
                            Object newDefaultElemValue = Utils.constructUnsafely(fieldTypeParam);
                            return new MultiElementListEntry<>(new TranslationTextComponent(classI13n), newDefaultElemValue, (List) getChildren(classI13n, fieldTypeParam, newDefaultElemValue, defaultElemValue, registry1), true);
                        } else
                            return new MultiElementListEntry<>(new TranslationTextComponent(classI13n), elem, (List) getChildren(classI13n, fieldTypeParam, elem, defaultElemValue, registry1), true);
                    }
                )
            );
        }, isNotListOfType(Integer.class, Long.class, Float.class, Double.class, String.class));

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startBooleanToggle(
                    new TranslationTextComponent(i13n),
                    getUnsafely(field, config, false)
                )
                    .setDefaultValue(() -> getUnsafely(field, defaults))
                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                    .setYesNoTextSupplier(bool -> {
                        String key = i13n + ".boolean." + bool;
                        String translate = I18n.format(key);
                        if (translate.equals(key))
                            return new TranslationTextComponent("text.cloth-config.boolean.value." + bool);
                        return new StringTextComponent(translate);
                    })
                    .build()
            ),
            boolean.class, Boolean.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startIntField(
                    new TranslationTextComponent(i13n),
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
                    new TranslationTextComponent(i13n),
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
                    new TranslationTextComponent(i13n),
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
                    new TranslationTextComponent(i13n),
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
                    new TranslationTextComponent(i13n),
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
                    new TranslationTextComponent(i13n),
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
        return getChildren(i13n, field.getType(), getUnsafely(field, config), getUnsafely(field, defaults), guiProvider);
    }

    private static List<AbstractConfigListEntry> getChildren(String i13n, Class<?> fieldType, Object iConfig, Object iDefaults, GuiRegistryAccess guiProvider) {
        return Arrays.stream(fieldType.getDeclaredFields())
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

    /**
     * Returns a predicate that tests if the field is a list containing some particular {@link Type}s, i.e. {@code List<Integer>}.
     *
     * @param types the types to check for in the list's parameter
     * @return {@code true} if the field is a list containing the provided type, {@code false} otherwise
     */
    private static Predicate<Field> isListOfType(Type... types) {
        return field -> {
            if (List.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                return args.length == 1 && Stream.of(types).anyMatch(type -> Objects.equals(args[0], type));
            } else {
                return false;
            }
        };
    }

    /**
     * Returns a predicate that tests if the field is a list <i>not</i> containing any particular {@link Type}s, i.e. anything that isn't a {@code List<Integer>}.
     *
     * @param types the types to check for in the list's parameter
     * @return {@code true} if the field is a list <i>not</i> containing any of the provided types, {@code false} otherwise
     */
    private static Predicate<Field> isNotListOfType(Type... types) {
        return field -> {
            if (List.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                return args.length == 1 && Stream.of(types).noneMatch(type -> Objects.equals(args[0], type));
            } else {
                return false;
            }
        };
    }
}
