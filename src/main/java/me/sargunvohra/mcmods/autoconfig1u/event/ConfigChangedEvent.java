package me.sargunvohra.mcmods.autoconfig1u.event;

import me.sargunvohra.mcmods.autoconfig1u.ConfigManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface ConfigChangedEvent {
    /**
     *  Callback that is called before the config manager serializes its config values
     *  This is called on initialization or when the config screen is saved
     *
     *  The callback uses an ActionResult to determine further actions
     * - SUCCESS stops any extra processing and uses the default behavior along
     * - PASS falls back to further processing and defaults to SUCCESS if no other listeners are available
     * - FAIL cancels further processing (the equivalent of returning the method)
     *
     * Also avoid calling {@link ConfigManager#save()} in this callback, as it
     * will result in an exception
     *
     * This is also called in {@link ConfigManager#load()} if the config file was edited
     * manually. It is called specifically on initialization
     *
     */
    Event<ConfigChangedEvent.Save> SAVED = EventFactory.createArrayBacked(ConfigChangedEvent.Save.class,
        (listeners) -> (config) -> {
            for (ConfigChangedEvent.Save listener : listeners) {
                ActionResult result = listener.onSave(config);
                if(result != ActionResult.PASS) {
                    return result;
                }
            }

            return ActionResult.PASS;
        });
    @FunctionalInterface
    interface Save{
        ActionResult onSave(ConfigManager manager);
    }
}
