package me.sargunvohra.mcmods.autoconfig.api;

import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface ConfigGuiProvider {
    @Nullable
    ClothConfigScreen.AbstractListEntry get(
        String i13n,
        Field field,
        ConfigData config,
        ConfigData defaults
    );
}
