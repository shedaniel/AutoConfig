package me.sargunvohra.mcmods.autoconfig.example;

import me.sargunvohra.mcmods.autoconfig.api.AutoConfig;
import me.sargunvohra.mcmods.autoconfig.api.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class ExampleInit implements ModInitializer {
    static final String CONFIG = "autoconfig_example";

    @Override
    public void onInitialize() {
        AutoConfig.register(CONFIG, ExampleConfig.class, Toml4jConfigSerializer::new);

        //noinspection unused
        ExampleConfig config = AutoConfig.<ExampleConfig>getConfigHolder(CONFIG).getConfig();
    }
}
