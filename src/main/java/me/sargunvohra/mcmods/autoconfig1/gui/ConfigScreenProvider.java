package me.sargunvohra.mcmods.autoconfig1.gui;

import me.sargunvohra.mcmods.autoconfig1.ConfigData;
import me.sargunvohra.mcmods.autoconfig1.ConfigManager;
import me.sargunvohra.mcmods.autoconfig1.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Environment(EnvType.CLIENT)
public class ConfigScreenProvider<T extends ConfigData> implements Supplier<Screen> {

    private final ConfigManager<T> manager;
    private final GuiRegistryAccess registry;
    private final Screen parent;

    public ConfigScreenProvider(
        ConfigManager<T> manager,
        GuiRegistryAccess registry,
        Screen parent
    ) {
        this.manager = manager;
        this.registry = registry;
        this.parent = parent;
    }


    @Override
    public Screen get() {
        T config = manager.getConfig();
        T defaults = manager.getSerializer().createDefault();

        String i13n = String.format("text.autoconfig.%s", manager.getDefinition().name());

        ClothConfigScreen.Builder builder = new ClothConfigScreen.Builder(
            parent, String.format("%s.title", i13n), (savedConfig) -> manager.save());

        Class<T> configClass = manager.getConfigClass();

        if (configClass.isAnnotationPresent(Config.Gui.Background.class)) {
            String bg = configClass.getAnnotation(Config.Gui.Background.class).value();
            Identifier bgId = Identifier.create(bg);
            builder.setBackgroundTexture(bgId);
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
                    field -> {
                        String categoryName = "default";

                        if (field.isAnnotationPresent(ConfigEntry.Category.class))
                            categoryName = field.getAnnotation(ConfigEntry.Category.class).value();

                        String categoryKey = String.format("%s.category.%s", i13n, categoryName);

                        if (!builder.hasCategory(categoryKey)) {
                            ConfigScreenBuilder.CategoryBuilder category = builder.addCategory(categoryKey);
                            if (categoryBackgrounds.containsKey(categoryKey)) {
                                category.setBackgroundTexture(categoryBackgrounds.get(categoryName));
                            }
                            return category;
                        }

                        return builder.getCategory(categoryKey);
                    }
                )
            )
            .forEach(
                (key, value) -> value.forEach(
                    field -> {
                        String optionI13n = String.format("%s.option.%s", i13n, field.getName());
                        registry.getAndTransform(optionI13n, field, config, defaults, registry)
                            .forEach(key::addOption);
                    }
                )
            );

        return builder.build();
    }
}
