package me.sargunvohra.mcmods.autoconfig1.example;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused") // entrypoint
@Environment(EnvType.CLIENT)
public class ExampleModMenuCompat implements ModMenuApi {
    @Override
    public String getModId() {
        return "autoconfig1";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(ExampleConfig.class, screen).get();
    }
}
