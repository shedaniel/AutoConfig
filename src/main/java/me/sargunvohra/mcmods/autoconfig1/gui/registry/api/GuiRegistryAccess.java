package me.sargunvohra.mcmods.autoconfig1.gui.registry.api;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.List;

@Environment(EnvType.CLIENT)
public interface GuiRegistryAccess extends GuiProvider, GuiTransformer {
    default List<ClothConfigScreen.AbstractListEntry> getAndTransform(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        GuiRegistryAccess registry
    ) {
        return transform(get(i13n, field, config, defaults, registry), i13n, field, config, defaults, registry);
    }
}
