package me.shedaniel.autoconfig1u;

public interface ConfigHolder<T extends ConfigData> {
    T getConfig();
}
