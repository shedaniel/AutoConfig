package me.sargunvohra.mcmods.autoconfig1u.gui.registry;

import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ComposedGuiRegistryAccess implements GuiRegistryAccess {

    private List<GuiRegistryAccess> children;

    public ComposedGuiRegistryAccess(GuiRegistryAccess... children) {
        this.children = Arrays.asList(children);
    }

    @Override
    public List<AbstractConfigListEntry<?>> get(
        String i13n,
        Field field,
        Object config,
        Object defaults,
        GuiRegistryAccess registry) {
        return children.stream()
            .map(child -> child.get(i13n, field, config, defaults, registry))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No ConfigGuiProvider match!"));
    }

    @Override
    public List<AbstractConfigListEntry<?>> transform(
        List<AbstractConfigListEntry<?>> guis,
        String i13n,
        Field field,
        Object config,
        Object defaults,
        GuiRegistryAccess registry
    ) {
        for (GuiRegistryAccess child : children) {
            guis = child.transform(guis, i13n, field, config, defaults, registry);
        }
        return guis;
    }
}
