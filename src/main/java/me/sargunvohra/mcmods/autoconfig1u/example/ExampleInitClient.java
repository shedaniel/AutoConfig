package me.sargunvohra.mcmods.autoconfig1u.example;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.ClientModInitializer;

@SuppressWarnings("unused") // entrypoint
public class ExampleInitClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // how to get the gui registry for custom gui handlers
        AutoConfig.getGuiRegistry(ExampleConfig.class);
    }
}
