package me.sargunvohra.mcmods.autoconfig1u.gui;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.GuiRegistry;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.sargunvohra.mcmods.autoconfig1u.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.getUnsafely;
import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.setUnsafely;

@Environment(EnvType.CLIENT)
public class DefaultGuiProviders {

    private static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();
    private static final Function<Enum<?>, String> DEFAULT_NAME_PROVIDER = t -> I18n.translate(t instanceof SelectionListEntry.Translatable ? ((SelectionListEntry.Translatable) t).getKey() : t.name());

    private DefaultGuiProviders() {
    }

    public static GuiRegistry apply(GuiRegistry registry) {

        registerAnnotationProviders(registry);

        registerPredicateProviders(registry);

        registerTypeProviders(registry);

        return registry;
    }

    private static void registerTypeProviders(GuiRegistry registry) {
        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startBooleanToggle(
                    i13n,
                    getUnsafely(field, config, false)
                )
                    .setDefaultValue(() -> getUnsafely(field, defaults))
                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                    .setYesNoTextSupplier(bool -> {
                        String key = i13n + ".boolean." + bool;
                        String translate = I18n.translate(key);
                        if (translate.equals(key))
                            return I18n.translate("text.cloth-config.boolean.value." + bool);
                        return translate;
                    })
                    .build()
            ),
            boolean.class, Boolean.class
        );

        registry.registerTypeProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startIntField(
                    i13n,
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
                ENTRY_BUILDER.startLongField(
                    i13n,
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
                    i13n,
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
                    i13n,
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
                    i13n,
                    getUnsafely(field, config, "")
                )
                    .setDefaultValue(() -> getUnsafely(field, defaults))
                    .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                    .build()
            ),
            String.class
        );
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void registerPredicateProviders(GuiRegistry registry) {
        registry.registerPredicateProvider(
            (i13n, field, config, defaults, guiProvider) -> {
                @SuppressWarnings("unchecked")
                List<Enum<?>> enums = Arrays.asList(((Class<? extends Enum<?>>) field.getType()).getEnumConstants());

                return Collections.singletonList(
                    ENTRY_BUILDER.startDropdownMenu(
                        i13n,
                        DropdownMenuBuilder.TopCellElementBuilder.of(
                            getUnsafely(field, config),
                            str -> {
                                for (Enum<?> constant : enums) {
                                    if (DEFAULT_NAME_PROVIDER.apply(constant).equals(str)) {
                                        return constant;
                                    }
                                }
                                return null;
                            },
                            DEFAULT_NAME_PROVIDER
                        ),
                        DropdownMenuBuilder.CellCreatorBuilder.of(
                            DEFAULT_NAME_PROVIDER
                        )
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
            ENTRY_BUILDER.startIntList(i13n, getUnsafely(field, config))
                .setDefaultValue(() -> getUnsafely(field, defaults))
                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                .build()
        ), isListOfType(Integer.class));

        registry.registerPredicateProvider((i13n, field, config, defaults, registry1) -> Collections.singletonList(
            ENTRY_BUILDER.startLongList(i13n, getUnsafely(field, config))
                .setDefaultValue(() -> getUnsafely(field, defaults))
                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                .build()
        ), isListOfType(Long.class));

        registry.registerPredicateProvider((i13n, field, config, defaults, registry1) -> Collections.singletonList(
            ENTRY_BUILDER.startFloatList(i13n, getUnsafely(field, config))
                .setDefaultValue(() -> getUnsafely(field, defaults))
                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                .build()
        ), isListOfType(Float.class));

        registry.registerPredicateProvider((i13n, field, config, defaults, registry1) -> Collections.singletonList(
            ENTRY_BUILDER.startDoubleList(i13n, getUnsafely(field, config))
                .setDefaultValue(() -> getUnsafely(field, defaults))
                .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                .build()
        ), isListOfType(Double.class));

        registry.registerPredicateProvider((i13n, field, config, defaults, registry1) -> Collections.singletonList(
            ENTRY_BUILDER.startStrList(i13n, getUnsafely(field, config))
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
                    i13n,
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
                            return new MultiElementListEntry<>(classI13n, newDefaultElemValue, getChildren(classI13n, fieldTypeParam, newDefaultElemValue, defaultElemValue, registry1), true);
                        } else
                            return new MultiElementListEntry<>(classI13n, elem, getChildren(classI13n, fieldTypeParam, elem, defaultElemValue, registry1), true);
                    }
                )
            );
        }, isNotListOfType(Integer.class, Long.class, Float.class, Double.class, String.class));
    }

    private static void registerAnnotationProviders(GuiRegistry registry) {
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
                        i13n,
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
                        i13n,
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
            DefaultGuiProviders::getChildren,
            field -> !field.getType().isPrimitive(),
            ConfigEntry.Gui.TransitiveObject.class
        );

        registry.registerAnnotationProvider(
            (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                ENTRY_BUILDER.startSubCategory(
                    i13n,
                    new ArrayList<>(getChildren(i13n, field, config, defaults, guiProvider))
                )
                    .setExpanded(field.getAnnotation(ConfigEntry.Gui.CollapsibleObject.class).startExpanded())
                    .build()
            ),
            field -> !field.getType().isPrimitive(),
            ConfigEntry.Gui.CollapsibleObject.class
        );
    }

    private static List<AbstractConfigListEntry<?>> getChildren(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess guiProvider) {
        return getChildren(i13n, field.getType(), getUnsafely(field, config), getUnsafely(field, defaults), guiProvider);
    }

    private static List<AbstractConfigListEntry<?>> getChildren(String i13n, Class<?> fieldType, Object iConfig, Object iDefaults, GuiRegistryAccess guiProvider) {

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
