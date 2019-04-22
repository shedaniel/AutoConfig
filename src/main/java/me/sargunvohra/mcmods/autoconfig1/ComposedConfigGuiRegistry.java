package me.sargunvohra.mcmods.autoconfig1;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
class ComposedConfigGuiRegistry implements ConfigGuiProviderTransformer {

    private List<ConfigGuiProviderTransformer> children;

    ComposedConfigGuiRegistry(ConfigGuiProviderTransformer... children) {
        this.children = Arrays.asList(children);
    }

    @Override
    public List<ClothConfigScreen.AbstractListEntry> get(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        ConfigGuiProviderTransformer registry) {
        return children.stream()
            .map(child -> child.get(i13n, field, config, defaults, registry))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No ConfigGuiProvider match!"));
    }

    @Override
    public List<ClothConfigScreen.AbstractListEntry> transform(
        List<ClothConfigScreen.AbstractListEntry> guis,
        String i13n,
        Field field,
        Object config,
        Object defaults,
        ConfigGuiProviderTransformer registry
    ) {
        for (ConfigGuiProviderTransformer child : children) {
            guis = child.transform(guis, i13n, field, config, defaults, registry);
        }
        return guis;
    }
}
