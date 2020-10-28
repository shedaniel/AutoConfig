package me.sargunvohra.mcmods.autoconfig1u;

import me.sargunvohra.mcmods.autoconfig1u.event.ConfigSerializeEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface ConfigHolder<T extends ConfigData> extends Supplier<T> {
    @NotNull
    Class<T> getConfigClass();

    void save();

    boolean load();

    T getConfig();

    void registerSaveListener(ConfigSerializeEvent.Save<T> save);

    void registerLoadListener(ConfigSerializeEvent.Load<T> load);

    @Override
    default T get() {
        return getConfig();
    }
}
