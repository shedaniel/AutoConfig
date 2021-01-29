package me.sargunvohra.mcmods.autoconfig1u.example;

import blue.endless.jankson.Comment;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "FieldMayBeFinal"})
@ApiStatus.Internal
@Config(name = "autoconfig1u_example")
@Config.Gui.Background("minecraft:textures/block/oak_planks.png")
@Config.Gui.CategoryBackground(category = "b", background = "minecraft:textures/block/stone.png")
public class ExampleConfig extends PartitioningSerializer.GlobalData {
    
    @ConfigEntry.Category("a")
    @ConfigEntry.Gui.TransitiveObject
    ModuleA moduleA = new ModuleA();
    
    @ConfigEntry.Category("a")
    @ConfigEntry.Gui.TransitiveObject
    Empty empty = new Empty();
    
    @ConfigEntry.Category("b")
    @ConfigEntry.Gui.TransitiveObject
    ModuleB moduleB = new ModuleB();
    
    enum ExampleEnum {
        FOO,
        BAR,
        BAZ
    }
    
    @Config(name = "module_a")
    private static class ModuleA implements ConfigData {
        
        @ConfigEntry.Gui.PrefixText
        private boolean aBoolean = true;
        
        @ConfigEntry.Gui.Tooltip(count = 2)
        private ExampleEnum anEnum = ExampleEnum.FOO;
        
        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        private ExampleEnum anEnumWithButton = ExampleEnum.FOO;
        
        @Comment("This tooltip was automatically applied from a Jankson @Comment")
        private String aString = "hello";
        
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        private PairOfIntPairs anObject = new PairOfIntPairs(new PairOfInts(), new PairOfInts(3, 4));
        
        private final List<Integer> list = Arrays.asList(1, 2, 3);
        
        private final List<PairOfInts> complexList = Arrays.asList(new PairOfInts(0, 1), new PairOfInts(3, 7));
    }
    
    @Config(name = "module_b")
    private static class ModuleB implements ConfigData {
        
        @ConfigEntry.BoundedDiscrete(min = -1000, max = 2000)
        private int intSlider = 500;
        
        @ConfigEntry.BoundedDiscrete(min = -1000, max = 2000)
        private Long longSlider = 500L;
        
        @ConfigEntry.Gui.TransitiveObject
        private PairOfIntPairs anObject = new PairOfIntPairs(new PairOfInts(), new PairOfInts(3, 4));
        
        @ConfigEntry.Gui.Excluded
        private List<PairOfInts> aList = Arrays.asList(new PairOfInts(), new PairOfInts(3, 4));
        
        @ConfigEntry.ColorPicker
        private int color = 0xFFFFFF;
    }
    
    @Config(name = "empty")
    private static class Empty implements ConfigData {
        
    }
    
    @SuppressWarnings("FieldCanBeLocal")
    private static class PairOfInts {
        private int foo;
        private int bar;
        
        PairOfInts() {
            this(1, 2);
        }
        
        PairOfInts(int foo, int bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }
    
    private static class PairOfIntPairs {
        
        @ConfigEntry.Gui.CollapsibleObject()
        PairOfInts first;
        
        @ConfigEntry.Gui.CollapsibleObject()
        PairOfInts second;
        
        PairOfIntPairs() {
            this(new PairOfInts(), new PairOfInts());
        }
        
        PairOfIntPairs(PairOfInts first, PairOfInts second) {
            this.first = first;
            this.second = second;
        }
    }
}
