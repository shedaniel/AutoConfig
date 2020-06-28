package me.shedaniel.autoconfig1u.gui.registry.api;

import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.util.List;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface GuiProvider {
    List<AbstractConfigListEntry> get(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        GuiRegistryAccess registry
    );
}
