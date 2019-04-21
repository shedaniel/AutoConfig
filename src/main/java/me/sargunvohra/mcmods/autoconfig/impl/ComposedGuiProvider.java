package me.sargunvohra.mcmods.autoconfig.impl;

import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiProvider;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ComposedGuiProvider implements ConfigGuiProvider {

    private static final Logger LOGGER = LogManager.getLogger();

    private List<ConfigGuiProvider> children;

    public ComposedGuiProvider(ConfigGuiProvider... children) {
        this.children = Arrays.asList(children);
    }

    @Override
    public List<ClothConfigScreen.AbstractListEntry> get(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        ConfigGuiProvider guiProvider) {
        return children.stream()
            .map(child -> child.get(i13n, field, config, defaults, guiProvider))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseGet(() -> {
                LOGGER.error("No GUI provider registered for field '{}'!", field);
                return null;
            });
    }
}
