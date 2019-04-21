package me.sargunvohra.mcmods.autoconfig.example;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig.api.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ExampleModMenuCompat implements ModMenuApi {
    @Override
    public String getModId() {
        return "autoconfig";
    }

    @Override
    public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
        return Optional.of(AutoConfig.getConfigScreen(ExampleInit.CONFIG, screen));
    }
}
