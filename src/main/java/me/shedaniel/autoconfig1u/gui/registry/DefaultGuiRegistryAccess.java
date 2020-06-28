package me.shedaniel.autoconfig1u.gui.registry;

import me.shedaniel.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class DefaultGuiRegistryAccess implements GuiRegistryAccess {
    @Override
    public List<AbstractConfigListEntry> get(
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
    public List<AbstractConfigListEntry> transform(
        List<AbstractConfigListEntry> guis,
        String i13n,
        Field field,
        Object config,
        Object defaults,
        GuiRegistryAccess registry
    ) {
        return guis;
    }
}
