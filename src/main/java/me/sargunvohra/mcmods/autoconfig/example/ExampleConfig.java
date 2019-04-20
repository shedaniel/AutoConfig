package me.sargunvohra.mcmods.autoconfig.example;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry;

public class ExampleConfig implements ConfigData {
    @ConfigGuiEntry
    public boolean aBoolean = false;

    @ConfigGuiEntry
    public Foo anEnum = Foo.TWO;

    @ConfigGuiEntry(category = "other")
    public String aString = "hello";

    @ConfigGuiEntry(category = "other")
    @ConfigGuiEntry.IntSlider(min = 0, max = 1000)
    public int aSlider = 500;

    public Bar hidden = new Bar();

    enum Foo {
        ONE, TWO, THREE
    }

    public static class Bar {
        public int a = 10;
        public int b = 20;
    }
}
