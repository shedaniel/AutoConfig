package me.sargunvohra.mcmods.autoconfig1;

import me.sargunvohra.mcmods.autoconfig1.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1.annotation.ConfigEntry;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.function.Supplier;

import static java.util.stream.Collectors.groupingBy;

@Environment(EnvType.CLIENT)
class ConfigScreenProvider<T extends ConfigData> implements Supplier<Screen> {

    private ConfigManager<T> manager;
    private ConfigGuiProvider guiProvider;
    private Screen parent;

    ConfigScreenProvider(
        ConfigManager<T> manager,
        ConfigGuiProvider guiProvider,
        Screen parent
    ) {
        this.manager = manager;
        this.guiProvider = guiProvider;
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

        Arrays.stream(configClass.getDeclaredFields())
            .collect(
                groupingBy(
                    field -> {
                        String category = "default";
                        if (field.isAnnotationPresent(ConfigEntry.Category.class))
                            category = field.getAnnotation(ConfigEntry.Category.class).value();
                        return builder.addCategory(String.format("%s.category.%s", i13n, category));
                    }
                )
            )
            .forEach(
                (key, value) -> value.forEach(
                    field ->
                        guiProvider.get(
                            String.format("%s.option.%s", i13n, field.getName()),
                            field, config, defaults, guiProvider
                        ).forEach(key::addOption)
                )
            );

        return builder.build();
    }
}
