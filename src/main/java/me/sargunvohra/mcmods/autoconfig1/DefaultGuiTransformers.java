package me.sargunvohra.mcmods.autoconfig1;

import blue.endless.jankson.Comment;
import me.sargunvohra.mcmods.autoconfig1.annotation.ConfigEntry;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.cloth.gui.entries.TextListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Language;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
class DefaultGuiTransformers {

    private DefaultGuiTransformers() {
    }

    static ConfigGuiRegistry apply(ConfigGuiRegistry registry) {

        registry.registerAnnotationTransformer(
            (guis, i13n, field, config, defaults, guiProvider) -> guis.stream()
                .peek(gui -> {
                    if (!(gui instanceof TextListEntry)) {
                        ConfigEntry.Gui.Tooltip tooltip = field.getAnnotation(ConfigEntry.Gui.Tooltip.class);
                        if (tooltip.count() == 1) {
                            tryApplyTooltip(
                                gui,
                                new String[]{
                                    Language.getInstance()
                                        .translate(String.format("%s.%s", i13n, "@Tooltip"))
                                }
                            );
                        } else {
                            tryApplyTooltip(
                                gui, IntStream.range(0, tooltip.count()).boxed()
                                    .map(i -> String.format("%s.%s[%d]", i13n, "@Tooltip", i))
                                    .map(Language.getInstance()::translate)
                                    .toArray(String[]::new)
                            );
                        }
                    }
                })
                .collect(Collectors.toList()),
            ConfigEntry.Gui.Tooltip.class
        );

        registry.registerAnnotationTransformer(
            (guis, i13n, field, config, defaults, guiProvider) -> guis.stream()
                .peek(gui -> {
                    if (!(gui instanceof TextListEntry)) {
                        Comment tooltip = field.getAnnotation(Comment.class);
                        String[] text = new String[]{tooltip.value()};
                        tryApplyTooltip(gui, text);
                    }
                })
                .collect(Collectors.toList()),
            Comment.class
        );

        registry.registerAnnotationTransformer(
            (guis, i13n, field, config, defaults, guiProvider) -> {
                ArrayList<ClothConfigScreen.AbstractListEntry> ret = new ArrayList<>(guis);
                String text = String.format("%s.%s", i13n, "@PrefixText");
                ret.add(0, new TextListEntry(text, Language.getInstance().translate(text)));
                return Collections.unmodifiableList(ret);
            },
            ConfigEntry.Gui.PrefixText.class
        );

        return registry;
    }

    private static Field findTooltipField(Class<?> cls) throws NoSuchFieldException {
        Class<?> current = cls;
        NoSuchFieldException firstException;
        do {
            try {
                return current.getDeclaredField("tooltipSupplier");
            } catch (NoSuchFieldException e) {
                firstException = e;
            }
        } while ((current = current.getSuperclass()) != null);
        throw firstException;
    }

    private static void tryApplyTooltip(ClothConfigScreen.AbstractListEntry gui, String[] text) {
        // yeah this is a really dirty hack
        // hoping for https://github.com/shedaniel/Cloth/issues/11 to clean it up
        try {
            Field f = findTooltipField(gui.getClass());
            f.setAccessible(true);
            f.set(gui, (Supplier<Optional<String[]>>) () -> Optional.of(text));
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
