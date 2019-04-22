package me.sargunvohra.mcmods.autoconfig1;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.List;

@Environment(EnvType.CLIENT)
public interface ConfigGuiProviderTransformer extends ConfigGuiProvider, ConfigGuiTransformer {
    default List<ClothConfigScreen.AbstractListEntry> getAndTransform(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        ConfigGuiProviderTransformer registry
    ) {
        return transform(get(i13n, field, config, defaults, registry), i13n, field, config, defaults, registry);
    }
}
