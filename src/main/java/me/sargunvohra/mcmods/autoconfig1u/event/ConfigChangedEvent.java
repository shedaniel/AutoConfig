package me.sargunvohra.mcmods.autoconfig1u.event;

import me.sargunvohra.mcmods.autoconfig1u.ConfigManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface ConfigChangedEvent {
    /**
     *  Callback that is called before the config manager serializes its config values
     *  The callback uses an ActionResult to determine further actions
     * - SUCCESS stops any extra processing and uses the default behavior
     * - PASS falls back to further processing and defaults to SUCCESS if no other listeners are available
     * - FAIL cancels further processing and does not serialize the config.
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
    interface Save{
        ActionResult onSave(ConfigManager manager);
    }
}
