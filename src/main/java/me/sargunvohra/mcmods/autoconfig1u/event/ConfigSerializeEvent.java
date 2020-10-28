package me.sargunvohra.mcmods.autoconfig1u.event;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import net.minecraft.util.ActionResult;

public final class ConfigSerializeEvent {
    private ConfigSerializeEvent() {}

    @FunctionalInterface
    public interface Save<T extends ConfigData> {
        /**
         * Callback that is called before the config manager serializes its config values
         * This is called on initialization or when the config screen is saved.
         * <p>
         * The callback uses an ActionResult to determine further actions
         * - SUCCESS stops any extra processing and uses the default behavior along
         * - PASS falls back to further processing and defaults to SUCCESS if no other listeners are available
         * - FAIL cancels further processing (the equivalent of returning the method)
         * <p>
         * Also avoid calling {@link ConfigHolder#save()} in this callback, as it
         * will result in an exception
         * <p>
         */
        ActionResult onSave(ConfigHolder<T> manager, T data);
    }

    @FunctionalInterface
    public interface Load<T extends ConfigData> {
        /**
         * Callback that is called after the config manager deserializes its config values
         * This is called on initialization or when the config screen is loaded.
         * <p>
         * This is also called in {@link ConfigHolder#load()} if the config file was edited
         * manually.
         * <p>
         * The callback uses an ActionResult to determine further actions
         * - SUCCESS stops any extra processing and uses the default behavior along
         * - PASS falls back to further processing and defaults to SUCCESS if no other listeners are available
         * - FAIL cancels further processing (the equivalent of returning the method)
         * <p>
         * Also avoid calling {@link ConfigHolder#load()} in this callback, as it
         * will result in an exception
         */
        ActionResult onLoad(ConfigHolder<T> manager, T newData);
    }
}
