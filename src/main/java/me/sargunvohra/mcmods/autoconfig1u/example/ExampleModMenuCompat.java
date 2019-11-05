package me.sargunvohra.mcmods.autoconfig1u.example;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

@SuppressWarnings("unused") // entrypoint
@Environment(EnvType.CLIENT)
public class ExampleModMenuCompat implements ModMenuApi {
    @Override
    public String getModId() {
        return "autoconfig1u";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(ExampleConfig.class, screen).get();
    }
}
