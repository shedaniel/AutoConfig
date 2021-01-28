package me.sargunvohra.mcmods.autoconfig1u.fabric.example;

import me.sargunvohra.mcmods.autoconfig1u.example.ExampleInits;
import net.fabricmc.api.ModInitializer;

@SuppressWarnings("unused") // entrypoint
public class ExampleInit implements ModInitializer {
    @Override
    public void onInitialize() {
        // check this class for an example on how to register and initialise a config
        ExampleInits.exampleCommonInit();
    }
}
