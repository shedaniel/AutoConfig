package me.shedaniel.autoconfig1u.gui.registry.api;

import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface GuiRegistryAccess extends GuiProvider, GuiTransformer {
    default List<AbstractConfigListEntry> getAndTransform(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        GuiRegistryAccess registry
    ) {
        return transform(get(i13n, field, config, defaults, registry), i13n, field, config, defaults, registry);
    }
}
