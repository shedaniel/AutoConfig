package me.sargunvohra.mcmods.autoconfig.impl;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry;
import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiProvider;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Environment(EnvType.CLIENT)
public class ConfigScreenProvider<T extends ConfigData> implements Supplier<Screen> {

    private static final Logger LOGGER = LogManager.getLogger();

    private ConfigManager<T> manager;
    private ConfigGuiProvider guiProvider;
    private Screen parent;

    public ConfigScreenProvider(
        ConfigManager<T> manager,
        ConfigGuiProvider guiProvider,
        Screen parent
    ) {
        this.manager = manager;
        this.guiProvider = guiProvider;
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

    @Override
    public Screen get() {
        T config = manager.getConfig();
        T defaults = manager.getSerializer().createDefault();

        String baseI13n = String.format("text.%s.config", manager.getName());

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
            field.setAccessible(true);
            String category = entry.category();
            String optionI13n = String.format("%s.option.%s.%s", baseI13n, category, field.getName());
            ClothConfigScreen.AbstractListEntry gui = guiProvider.get(optionI13n, field, config, defaults);

            if (gui != null) {
                categories.get(category).addOption(gui);
            } else {
                LOGGER.error("No GUI provider registered for field '{}'!", field);
            }
        });

        return builder.build();
    }
}
