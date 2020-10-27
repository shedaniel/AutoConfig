package me.sargunvohra.mcmods.autoconfig1u.example;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import me.sargunvohra.mcmods.autoconfig1u.event.ConfigChangedEvent;
import me.sargunvohra.mcmods.autoconfig1u.serializer.DummyConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.ActionResult;

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
        //this event allows you to change or register specific listeners
        // for when the config has changed
        ConfigChangedEvent.SAVED.register(manager -> {
         System.out.println(manager.getConfig());
            return ActionResult.SUCCESS;
        });
    }
}
