package me.sargunvohra.mcmods.autoconfig.example;

import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry;
import me.sargunvohra.mcmods.autoconfig.api.serializer.ModularSerializer;

@SuppressWarnings("unused")
public class ExampleConfig implements ModularSerializer.ModularConfigData {

    @ConfigGuiEntry(category = "a")
    @ConfigGuiEntry.Transitive
    ModuleA moduleA = new ModuleA();

    @ConfigGuiEntry(category = "b")
    @ConfigGuiEntry.Transitive
    ModuleB moduleB = new ModuleB();

    enum ExampleEnum {
        FOO, BAR, BAZ
    }

    private static class ModuleA implements ModularSerializer.Module {

        private boolean aBoolean = true;

        private ExampleEnum anEnum = ExampleEnum.FOO;

        private String aString = "hello";

        @ConfigGuiEntry.Transitive
        private TwoInts anObject = new TwoInts(1, 2);
    }

    private static class ModuleB implements ModularSerializer.Module {

        @ConfigGuiEntry.IntSlider(min = -1000, max = 2000)
        private int intSlider = 500;

        @ConfigGuiEntry.LongSlider(min = -1000L, max = 2000L)
        private long longSlider = 500L;

        @ConfigGuiEntry.Transitive
        private TwoInts anObject = new TwoInts(3, 4);
    }

    private static class TwoInts {
        int first;
        int second;

        TwoInts(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }
}
