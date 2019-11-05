package me.sargunvohra.mcmods.autoconfig1u.example;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import me.sargunvohra.mcmods.autoconfig1u.serializer.DummyConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;

@SuppressWarnings("unused") // entrypoint
public class ExampleInit implements ModInitializer {
    @Override
    public void onInitialize() {
        // how to register a config:
        ConfigHolder<ExampleConfig> holder = AutoConfig.register(
            ExampleConfig.class,
            PartitioningSerializer.wrap(DummyConfigSerializer::new)
        );

        // how to read a config:
        holder.getConfig();
        // or
        AutoConfig.getConfigHolder(ExampleConfig.class).getConfig();
    }
}
