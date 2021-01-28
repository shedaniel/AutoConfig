package me.sargunvohra.mcmods.autoconfig1u.example;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import me.sargunvohra.mcmods.autoconfig1u.serializer.DummyConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.InteractionResult;

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
        // or (please cache this value)
        AutoConfig.getConfigHolder(ExampleConfig.class).getConfig();
        // this event allows you to change or register specific listeners
        // for when the config has changed
        AutoConfig.getConfigHolder(ExampleConfig.class).registerSaveListener((manager, data) -> {
            return InteractionResult.SUCCESS;
        });
        AutoConfig.getConfigHolder(ExampleConfig.class).registerLoadListener((manager, newData) -> {
            return InteractionResult.SUCCESS;
        });
    }
}
