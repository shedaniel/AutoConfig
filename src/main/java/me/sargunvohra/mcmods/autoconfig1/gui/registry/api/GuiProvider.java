package me.sargunvohra.mcmods.autoconfig1.gui.registry.api;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.List;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface GuiProvider {
    List<ClothConfigScreen.AbstractListEntry> get(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        GuiRegistryAccess registry
    );
}
