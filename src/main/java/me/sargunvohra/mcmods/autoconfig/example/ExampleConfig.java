package me.sargunvohra.mcmods.autoconfig.example;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ExampleConfig implements ConfigData {
    @ConfigGuiEntry
    private boolean aBoolean = true;

    @ConfigGuiEntry
    private Foo anEnum = Foo.ONE;

    @ConfigGuiEntry(category = "other")
    private String aString = "hello";

    @ConfigGuiEntry(category = "other")
    @ConfigGuiEntry.IntSlider(min = 0, max = 1000)
    private int aSlider = 500;

    @ConfigGuiEntry(category = "nesting")
    @ConfigGuiEntry.Transitive
    private Bar anObject = new Bar();

    // fields without @ConfigGuiEntry are saved and loaded, but don't appear in the config gui
    private Bar aHiddenObject = new Bar();

    enum Foo {
        ONE, TWO, THREE
    }

    public static class Bar {

        // fields inside transitive objects don't need @ConfigGuiEntry
        // the category of each nested field is always the same as its top level field

        @ConfigGuiEntry.IntSlider(min = 0, max = 20)
        int a = 10;

        int b = 20;

        @Override
        public String toString() {
            return "Bar{" +
                "a=" + a +
                ", b=" + b +
                '}';
        }
    }

    @Override
    public String toString() {
        return "ExampleConfig{" +
            "aBoolean=" + aBoolean +
            ", anEnum=" + anEnum +
            ", aString='" + aString + '\'' +
            ", aSlider=" + aSlider +
            ", anObject=" + anObject +
            ", aHiddenObject=" + aHiddenObject +
            '}';
    }
}
