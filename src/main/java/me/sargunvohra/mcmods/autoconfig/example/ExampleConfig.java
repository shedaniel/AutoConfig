package me.sargunvohra.mcmods.autoconfig.example;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry;

public class ExampleConfig implements ConfigData {
    @ConfigGuiEntry
    public boolean aBoolean = true;

    @ConfigGuiEntry
    public Foo anEnum = Foo.ONE;

    @ConfigGuiEntry(category = "other")
    public String aString = "hello";

    @ConfigGuiEntry(category = "other")
    @ConfigGuiEntry.IntSlider(min = 0, max = 1000)
    public int aSlider = 500;

    @ConfigGuiEntry(category = "nesting")
    @ConfigGuiEntry.Transitive
    public Bar anObject = new Bar();

    // fields without @ConfigGuiEntry are saved and loaded, but don't appear in the config gui
    public Bar aHiddenObject = new Bar();

    enum Foo {
        ONE, TWO, THREE
    }

    public static class Bar {

        // fields inside transitive objects don't need @ConfigGuiEntry
        // the category of each nested field is always the same as its top level field

        @ConfigGuiEntry.IntSlider(min = 0, max = 20)
        public int a = 10;

        public int b = 20;
    }
}
