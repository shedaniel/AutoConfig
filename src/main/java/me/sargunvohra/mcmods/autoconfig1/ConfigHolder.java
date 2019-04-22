package me.sargunvohra.mcmods.autoconfig1;

public interface ConfigHolder<T extends ConfigData> {
    T getConfig();
}
