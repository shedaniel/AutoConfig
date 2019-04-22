package me.sargunvohra.mcmods.autoconfig1;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
class ComposedGuiProvider implements ConfigGuiProvider {

    private List<ConfigGuiProvider> children;

    ComposedGuiProvider(ConfigGuiProvider... children) {
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
            .orElseThrow(() -> new RuntimeException("No ConfigGuiProvider match!"));
    }
}
