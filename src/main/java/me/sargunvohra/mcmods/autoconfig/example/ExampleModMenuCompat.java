package me.sargunvohra.mcmods.autoconfig.example;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig.api.AutoConfig;
import net.minecraft.client.gui.Screen;

import java.util.Optional;
import java.util.function.Supplier;

public class ExampleModMenuCompat implements ModMenuApi {
    @Override
    public String getModId() {
        return ExampleInit.MOD_ID;
    }

    @Override
    public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
        return Optional.of(AutoConfig.getConfigScreen(getModId(), screen));
    }
}
