package me.sargunvohra.mcmods.autoconfig1;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

class FallbackGuiRegistry implements ConfigGuiProviderTransformer {
    @Override
    public List<ClothConfigScreen.AbstractListEntry> get(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        ConfigGuiProviderTransformer registry
    ) {
        LogManager.getLogger().error("No GUI provider registered for field '{}'!", field);
        return Collections.emptyList();
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
        return guis;
    }
}
