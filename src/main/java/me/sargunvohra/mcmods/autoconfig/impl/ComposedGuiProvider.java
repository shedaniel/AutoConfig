package me.sargunvohra.mcmods.autoconfig.impl;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiProvider;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ComposedGuiProvider implements ConfigGuiProvider {

    private List<ConfigGuiProvider> children;

    public ComposedGuiProvider(ConfigGuiProvider... children) {
        this.children = Arrays.asList(children);
    }

    @Override
    public ClothConfigScreen.AbstractListEntry get(
        String i13n,
        Field field,
        ConfigData config,
        ConfigData defaults
    ) {
        return children.stream()
            .map(child -> child.get(i13n, field, config, defaults))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
}
