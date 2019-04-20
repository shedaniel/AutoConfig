package me.sargunvohra.mcmods.autoconfig.example;

import me.sargunvohra.mcmods.autoconfig.api.AutoConfig;
import net.fabricmc.api.ModInitializer;

public class ExampleInit implements ModInitializer {
    static final String MOD_ID = "autoconfig";

    @Override
    public void onInitialize() {
        AutoConfig.registerDummy(MOD_ID, ExampleConfig.class);
    }
}
