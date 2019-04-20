package me.sargunvohra.mcmods.autoconfig.api;

public interface ConfigHolder<T extends ConfigData> {
    T getConfig();
}
