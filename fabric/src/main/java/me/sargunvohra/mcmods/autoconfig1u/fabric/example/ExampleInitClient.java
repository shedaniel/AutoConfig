package me.sargunvohra.mcmods.autoconfig1u.fabric.example;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.example.ExampleConfig;
import me.sargunvohra.mcmods.autoconfig1u.example.ExampleInits;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@SuppressWarnings("unused") // entrypoint
public class ExampleInitClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // check this class for an example on how to register custom gui handlers
        ExampleInits.exampleClientInit();
    }
}
