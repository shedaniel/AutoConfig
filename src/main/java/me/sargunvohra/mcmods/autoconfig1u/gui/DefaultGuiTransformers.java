package me.sargunvohra.mcmods.autoconfig1u.gui;

import blue.endless.jankson.Comment;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class DefaultGuiTransformers {

    private static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();

    private DefaultGuiTransformers() {
    }

    public static GuiRegistry apply(GuiRegistry registry) {

        registry.registerAnnotationTransformer(
            (guis, i13n, field, config, defaults, guiProvider) -> guis.stream()
                .peek(gui -> {
                    if (!(gui instanceof TextListEntry)) {
                        ConfigEntry.Gui.Tooltip tooltip = field.getAnnotation(ConfigEntry.Gui.Tooltip.class);
                        if (tooltip.count() == 0) {
                            tryRemoveTooltip(gui);
                        } else if (tooltip.count() == 1) {
                            tryApplyTooltip(
                                gui,
                                new Text[]{
                                    new TranslatableText(String.format("%s.%s", i13n, "@Tooltip"))
                                }
                            );
                        } else {
                            tryApplyTooltip(
                                gui, IntStream.range(0, tooltip.count()).boxed()
                                    .map(i -> String.format("%s.%s[%d]", i13n, "@Tooltip", i))
                                    .map(TranslatableText::new)
                                    .toArray(Text[]::new)
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
                        Text[] text = new Text[]{new LiteralText(tooltip.value())};
                        tryApplyTooltip(gui, text);
                    }
                })
                .collect(Collectors.toList()),
            field -> !field.isAnnotationPresent(ConfigEntry.Gui.Tooltip.class),
            Comment.class
        );

        registry.registerAnnotationTransformer(
            (guis, i13n, field, config, defaults, guiProvider) -> guis.stream()
                .peek(gui -> {
                    if (!(gui instanceof TextListEntry)) {
                        tryRemoveTooltip(gui, text);
                    }
                })
                .collect(Collectors.toList()),
            ConfigEntry.Gui.NoTooltip.class
        );

        registry.registerAnnotationTransformer(
            (guis, i13n, field, config, defaults, guiProvider) -> {
                ArrayList<AbstractConfigListEntry> ret = new ArrayList<>(guis);
                String text = String.format("%s.%s", i13n, "@PrefixText");
                ret.add(0, ENTRY_BUILDER.startTextDescription(new TranslatableText(text)).build());
                return Collections.unmodifiableList(ret);
            },
            ConfigEntry.Gui.PrefixText.class
        );

        registry.registerAnnotationTransformer(
            (guis, i13n, field, config, defaults, guiProvider) -> {
                for (AbstractConfigListEntry gui : guis) {
                    gui.setRequiresRestart(field.getAnnotation(ConfigEntry.Gui.RequiresRestart.class).value());
                }
                return guis;
            },
            ConfigEntry.Gui.RequiresRestart.class
        );

        return registry;
    }

    private static void tryApplyTooltip(AbstractConfigListEntry gui, Text[] text) {
        if (gui instanceof TooltipListEntry) {
            TooltipListEntry tooltipGui = (TooltipListEntry) gui;
            tooltipGui.setTooltipSupplier(() -> Optional.of(text));
        }
    }

    private static void tryRemoveTooltip(AbstractConfigListEntry gui) {
        if (gui instanceof TooltipListEntry) {
            TooltipListEntry tooltipGui = (TooltipListEntry) gui;
            tooltipGui.setTooltipSupplier(() -> Optional.empty());
        }
    }
}
