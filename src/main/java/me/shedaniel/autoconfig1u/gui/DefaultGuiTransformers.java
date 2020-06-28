package me.shedaniel.autoconfig1u.gui;

import blue.endless.jankson.Comment;
import me.shedaniel.autoconfig1u.annotation.ConfigEntry;
import me.shedaniel.autoconfig1u.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.forge.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.forge.gui.entries.TextListEntry;
import me.shedaniel.clothconfig2.forge.gui.entries.TooltipListEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
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
                        if (tooltip.count() == 1) {
                            tryApplyTooltip(
                                gui,
                                new ITextComponent[]{
                                    new TranslationTextComponent(String.format("%s.%s", i13n, "@Tooltip"))
                                }
                            );
                        } else {
                            tryApplyTooltip(
                                gui, IntStream.range(0, tooltip.count()).boxed()
                                    .map(i -> String.format("%s.%s[%d]", i13n, "@Tooltip", i))
                                    .map(TranslationTextComponent::new)
                                    .toArray(ITextComponent[]::new)
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
                        ITextComponent[] text = new ITextComponent[]{new StringTextComponent(tooltip.value())};
                        tryApplyTooltip(gui, text);
                    }
                })
                .collect(Collectors.toList()),
            field -> !field.isAnnotationPresent(ConfigEntry.Gui.Tooltip.class),
            Comment.class
        );

        registry.registerAnnotationTransformer(
            (guis, i13n, field, config, defaults, guiProvider) -> {
                ArrayList<AbstractConfigListEntry> ret = new ArrayList<>(guis);
                String text = String.format("%s.%s", i13n, "@PrefixText");
                ret.add(0, ENTRY_BUILDER.startTextDescription(new TranslationTextComponent(text)).build());
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

    private static void tryApplyTooltip(AbstractConfigListEntry gui, ITextComponent[] text) {
        if (gui instanceof TooltipListEntry) {
            TooltipListEntry tooltipGui = (TooltipListEntry) gui;
            tooltipGui.setTooltipSupplier(() -> Optional.of(text));
        }
    }
}
