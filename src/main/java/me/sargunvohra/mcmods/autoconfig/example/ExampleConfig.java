package me.sargunvohra.mcmods.autoconfig.example;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry;
import me.sargunvohra.mcmods.autoconfig.api.serializer.PartitioningSerializer;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class ExampleConfig extends PartitioningSerializer.GlobalData {

    @ConfigGuiEntry(category = "a")
    @ConfigGuiEntry.Transitive
    ModuleA moduleA = new ModuleA();

    @ConfigGuiEntry(category = "b")
    @ConfigGuiEntry.Transitive
    ModuleB moduleB = new ModuleB();

    enum ExampleEnum {
        FOO, BAR, BAZ
    }

    private static class ModuleA implements ConfigData {

        private boolean aBoolean = true;

        private ExampleEnum anEnum = ExampleEnum.FOO;

        private String aString = "hello";

        @ConfigGuiEntry.Transitive
        private TwoInts anObject = new TwoInts(1, 2);
    }

    private static class ModuleB implements ConfigData {

        @ConfigGuiEntry.IntSlider(min = -1000, max = 2000)
        private int intSlider = 500;

        @ConfigGuiEntry.LongSlider(min = -1000L, max = 2000L)
        private long longSlider = 500L;

        @ConfigGuiEntry.Transitive
        private TwoInts anObject = new TwoInts(3, 4);

        @ConfigGuiEntry.Exclude
        private List<ListItem> aList = Arrays.asList(new ListItem(), new ListItem(3, 4));
    }

    private static class ListItem {
        private int foo;
        private int bar;

        public ListItem() {
            foo = 1;
            bar = 2;
        }

        public ListItem(int foo, int bar) {
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
