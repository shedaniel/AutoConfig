package me.sargunvohra.mcmods.autoconfig1u.gui;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.ConfigManager;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.*;

@Environment(EnvType.CLIENT)
public class ConfigScreenProvider<T extends ConfigData> implements Supplier<Screen> {

    private static final Identifier TRANSPARENT_BACKGROUND = new Identifier(Config.Gui.Background.TRANSPARENT);

    private final ConfigManager<T> manager;
    private final GuiRegistryAccess registry;
    private final Screen parent;
    private Function<ConfigManager<T>, String> i13nFunction = manager -> String.format("text.autoconfig.%s", manager.getDefinition().name());
    private Function<ConfigBuilder, Screen> buildFunction = ConfigBuilder::build;
    private BiFunction<String, Field, String> optionFunction = (baseI13n, field) -> String.format("%s.option.%s", baseI13n, field.getName());
    private BiFunction<String, String, String> categoryFunction = (baseI13n, categoryName) -> String.format("%s.category.%s", baseI13n, categoryName);

    public ConfigScreenProvider(
        ConfigManager<T> manager,
        GuiRegistryAccess registry,
        Screen parent
    ) {
        this.manager = manager;
        this.registry = registry;
        this.parent = parent;
    }

    @Deprecated
    public void setI13nFunction(Function<ConfigManager<T>, String> i13nFunction) {
        this.i13nFunction = i13nFunction;
    }

    @Deprecated
    public void setBuildFunction(Function<ConfigBuilder, Screen> buildFunction) {
        this.buildFunction = buildFunction;
    }

    @Deprecated
    public void setCategoryFunction(BiFunction<String, String, String> categoryFunction) {
        this.categoryFunction = categoryFunction;
    }

    @Deprecated
    public void setOptionFunction(BiFunction<String, Field, String> optionFunction) {
        this.optionFunction = optionFunction;
    }

    @Override
    public Screen get() {
        T config = manager.getConfig();
        T defaults = manager.getSerializer().createDefault();

        String i13n = i13nFunction.apply(manager);

        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(String.format("%s.title", i13n)).setSavingRunnable(manager::save);

        Class<T> configClass = manager.getConfigClass();

        if (configClass.isAnnotationPresent(Config.Gui.Background.class)) {
            String bg = configClass.getAnnotation(Config.Gui.Background.class).value();
            Identifier bgId = Identifier.tryParse(bg);
            if (TRANSPARENT_BACKGROUND.equals(bgId))
                builder.transparentBackground();
            else
                builder.setDefaultBackgroundTexture(bgId);
        }

        Map<String, Identifier> categoryBackgrounds =
            Arrays.stream(configClass.getAnnotationsByType(Config.Gui.CategoryBackground.class))
                .collect(
                    toMap(
                        Config.Gui.CategoryBackground::category,
                        ann -> new Identifier(ann.background())
                    )
                );

        Arrays.stream(configClass.getDeclaredFields())
            .collect(
                groupingBy(
                    field -> getOrCreateCategoryForField(field, builder, categoryBackgrounds, i13n),
                    LinkedHashMap::new,
                    toList()
                )
            )
            .forEach(
                (key, value) -> value.forEach(
                    field -> {
                        String optionI13n = optionFunction.apply(i13n, field);
                        registry.getAndTransform(optionI13n, field, config, defaults, registry)
                            .forEach(key::addEntry);
                    }
                )
            );

        return buildFunction.apply(builder);
    }

    private ConfigCategory getOrCreateCategoryForField(
        Field field,
        ConfigBuilder screenBuilder,
        Map<String, Identifier> backgroundMap,
        String baseI13n
    ) {
        String categoryName = "default";

        if (field.isAnnotationPresent(ConfigEntry.Category.class))
            categoryName = field.getAnnotation(ConfigEntry.Category.class).value();

        String categoryKey = categoryFunction.apply(baseI13n, categoryName);

        if (!screenBuilder.hasCategory(categoryKey)) {
            ConfigCategory category = screenBuilder.getOrCreateCategory(categoryKey);
            if (backgroundMap.containsKey(categoryName)) {
                category.setCategoryBackground(backgroundMap.get(categoryName));
            }
            return category;
        }

        return screenBuilder.getOrCreateCategory(categoryKey);
    }
}
