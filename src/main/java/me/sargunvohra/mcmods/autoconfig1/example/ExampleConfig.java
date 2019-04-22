package me.sargunvohra.mcmods.autoconfig1.example;

import me.sargunvohra.mcmods.autoconfig1.ConfigData;
import me.sargunvohra.mcmods.autoconfig1.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1.serializer.PartitioningSerializer;

import java.util.Arrays;
import java.util.List;

@Config(name = "autoconfig_example")
class ExampleConfig extends PartitioningSerializer.GlobalData {

    @ConfigEntry.Category("a")
    @ConfigEntry.Gui.TransitiveObject
    ModuleA moduleA = new ModuleA();

    @ConfigEntry.Category("b")
    @ConfigEntry.Gui.TransitiveObject
    ModuleB moduleB = new ModuleB();

    enum ExampleEnum {
        FOO, BAR, BAZ
    }

    private static class ModuleA implements ConfigData {

        private boolean aBoolean = true;

        private ExampleEnum anEnum = ExampleEnum.FOO;

        private String aString = "hello";

        @ConfigEntry.Gui.TransitiveObject
        private TwoInts anObject = new TwoInts(1, 2);
    }

    private static class ModuleB implements ConfigData {

        @ConfigEntry.BoundedDiscrete(min = -1000, max = 2000)
        private int intSlider = 500;

        @ConfigEntry.BoundedDiscrete(min = -1000, max = 2000)
        private Long longSlider = 500L;

        @ConfigEntry.Gui.TransitiveObject
        private TwoInts anObject = new TwoInts(3, 4);

        @ConfigEntry.Gui.Excluded
        private List<ListItem> aList = Arrays.asList(new ListItem(), new ListItem(3, 4));
    }

    private static class ListItem {
        private int foo;
        private int bar;

        ListItem() {
            foo = 1;
            bar = 2;
        }

        ListItem(int foo, int bar) {
            this.foo = foo;
            this.bar = bar;
        }
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
