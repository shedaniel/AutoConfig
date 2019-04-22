package me.sargunvohra.mcmods.autoconfig1;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.List;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface ConfigGuiTransformer {
    List<ClothConfigScreen.AbstractListEntry> transform(
        List<ClothConfigScreen.AbstractListEntry> guis,
        String i13n,
        Field field,
        Object config,
        Object defaults,
        ConfigGuiProviderTransformer registry
    );
}
