package me.sargunvohra.mcmods.autoconfig1u;

public interface ConfigHolder<T extends ConfigData> {
    T getConfig();
}
