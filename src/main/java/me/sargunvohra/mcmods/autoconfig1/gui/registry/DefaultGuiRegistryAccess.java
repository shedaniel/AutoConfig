package me.sargunvohra.mcmods.autoconfig1.gui.registry;

import me.sargunvohra.mcmods.autoconfig1.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class DefaultGuiRegistryAccess implements GuiRegistryAccess {
    @Override
    public List<ClothConfigScreen.AbstractListEntry> get(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        GuiRegistryAccess registry
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
        GuiRegistryAccess registry
    ) {
        return guis;
    }
}
