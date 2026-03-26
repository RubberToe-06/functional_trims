package functional_trims.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class FunctionalTrimsConfigScreen {
    private FunctionalTrimsConfigScreen() {
    }

    public static Screen create(Screen parent) {
        FunctionalTrimsConfig cfg = ConfigManager.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.functional_trims.title"));

        builder.setSavingRunnable(ConfigManager::save);

        ConfigEntryBuilder entry = builder.entryBuilder();
        ConfigCategory materials = builder.getOrCreateCategory(
                Text.translatable("config.functional_trims.category.trim_materials")
        );

        materials.addEntry(boolEntry(
                entry,
                "config.functional_trims.enable_all",
                "config.functional_trims.enable_all.tooltip",
                () -> cfg.modEnabled,
                val -> cfg.modEnabled = val
        ));

        addIron(entry, materials, cfg);
        addGold(entry, materials, cfg);
        addDiamond(entry, materials, cfg);
        addNetherite(entry, materials, cfg);
        addRedstone(entry, materials, cfg);
        addEmerald(entry, materials, cfg);
        addLapis(entry, materials, cfg);
        addQuartz(entry, materials, cfg);
        addAmethyst(entry, materials, cfg);
        addCopper(entry, materials, cfg);
        addResin(entry, materials, cfg);

        return builder.build();
    }

    private static void addIron(ConfigEntryBuilder entry, ConfigCategory category, FunctionalTrimsConfig cfg) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        addEnabledToggle(entry, entries, cfg.iron);
        entries.add(floatEntry(entry, "config.functional_trims.iron.reflect",
                () -> cfg.iron.projectileReflectChance, 0.6f, 0.0f, 1.0f,
                val -> cfg.iron.projectileReflectChance = val));
        entries.add(floatEntry(entry, "config.functional_trims.iron.knockback",
                () -> cfg.iron.shieldKnockbackStrengthMultiplier, 1.15f, 0.0f, 5.0f,
                val -> cfg.iron.shieldKnockbackStrengthMultiplier = val));
        entries.add(boolEntry(entry, "config.functional_trims.iron.axe_resist", null,
                () -> cfg.iron.axeAttackResistanceEnabled,
                val -> cfg.iron.axeAttackResistanceEnabled = val));

        category.addEntry(subCategory(entry, "config.functional_trims.iron", "config.functional_trims.iron.tooltip"));
    }

    private static void addGold(ConfigEntryBuilder entry, ConfigCategory category, FunctionalTrimsConfig cfg) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        addEnabledToggle(entry, entries, cfg.gold);
        entries.add(boolEntry(entry, "config.functional_trims.gold.distract", null,
                () -> cfg.gold.distractPiglinBrutesEnabled,
                val -> cfg.gold.distractPiglinBrutesEnabled = val));

        category.addEntry(subCategory(entry, "config.functional_trims.gold", "config.functional_trims.gold.tooltip"));
    }

    private static void addDiamond(ConfigEntryBuilder entry, ConfigCategory category, FunctionalTrimsConfig cfg) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        addEnabledToggle(entry, entries, cfg.diamond);
        entries.add(floatEntry(entry, "config.functional_trims.diamond.heal",
                () -> cfg.diamond.percentHealthRegainedAfterBurst, 0.5f, 0.0f, 1.0f,
                val -> cfg.diamond.percentHealthRegainedAfterBurst = val));
        entries.add(floatEntry(entry, "config.functional_trims.diamond.armor_loss",
                () -> cfg.diamond.percentArmorDurabilityLostAfterBurst, 1.0f, 0.0f, 1.0f,
                val -> cfg.diamond.percentArmorDurabilityLostAfterBurst = val));

        category.addEntry(subCategory(entry, "config.functional_trims.diamond", "config.functional_trims.diamond.tooltip"));
    }

    private static void addNetherite(ConfigEntryBuilder entry, ConfigCategory category, FunctionalTrimsConfig cfg) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        addEnabledToggle(entry, entries, cfg.netherite);

        category.addEntry(subCategory(entry, "config.functional_trims.netherite", "config.functional_trims.netherite.tooltip"));
    }

    private static void addRedstone(ConfigEntryBuilder entry, ConfigCategory category, FunctionalTrimsConfig cfg) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        addEnabledToggle(entry, entries, cfg.redstone);
        entries.add(intEntry(entry,
                () -> cfg.redstone.blockPowerLevelWhenSteppedOn,
                val -> cfg.redstone.blockPowerLevelWhenSteppedOn = val));

        category.addEntry(subCategory(entry, "config.functional_trims.redstone", "config.functional_trims.redstone.tooltip"));
    }

    private static void addEmerald(ConfigEntryBuilder entry, ConfigCategory category, FunctionalTrimsConfig cfg) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        addEnabledToggle(entry, entries, cfg.emerald);
        entries.add(floatEntry(entry, "config.functional_trims.emerald.roll1",
                () -> cfg.emerald.percentChanceForExtraRoll1, 1.0f, 0.0f, 1.0f,
                val -> cfg.emerald.percentChanceForExtraRoll1 = val));
        entries.add(floatEntry(entry, "config.functional_trims.emerald.roll2",
                () -> cfg.emerald.percentChanceForExtraRoll2, 0.5f, 0.0f, 1.0f,
                val -> cfg.emerald.percentChanceForExtraRoll2 = val));

        category.addEntry(subCategory(entry, "config.functional_trims.emerald", "config.functional_trims.emerald.tooltip"));
    }

    private static void addLapis(ConfigEntryBuilder entry, ConfigCategory category, FunctionalTrimsConfig cfg) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        addEnabledToggle(entry, entries, cfg.lapis);
        entries.add(floatEntry(entry, "config.functional_trims.lapis.exp",
                () -> cfg.lapis.extraExpMultiplier, 0.5f, 0.0f, 10.0f,
                val -> cfg.lapis.extraExpMultiplier = val));

        category.addEntry(subCategory(entry, "config.functional_trims.lapis", "config.functional_trims.lapis.tooltip"));
    }

    private static void addQuartz(ConfigEntryBuilder entry, ConfigCategory category, FunctionalTrimsConfig cfg) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        addEnabledToggle(entry, entries, cfg.quartz);
        entries.add(floatEntry(entry, "config.functional_trims.quartz.hunger",
                () -> cfg.quartz.hungerRestoredMultiplier, 1.25f, 0.0f, 10.0f,
                val -> cfg.quartz.hungerRestoredMultiplier = val));
        entries.add(floatEntry(entry, "config.functional_trims.quartz.saturation",
                () -> cfg.quartz.saturationRestoredMultiplier, 1.25f, 0.0f, 10.0f,
                val -> cfg.quartz.saturationRestoredMultiplier = val));
        entries.add(floatEntry(entry, "config.functional_trims.quartz.potion",
                () -> cfg.quartz.potionEffectDurationMultiplier, 1.25f, 0.0f, 10.0f,
                val -> cfg.quartz.potionEffectDurationMultiplier = val));

        category.addEntry(subCategory(entry, "config.functional_trims.quartz", "config.functional_trims.quartz.tooltip"));
    }

    private static void addAmethyst(ConfigEntryBuilder entry, ConfigCategory category, FunctionalTrimsConfig cfg) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        addEnabledToggle(entry, entries, cfg.amethyst);
        entries.add(floatEntry(entry, "config.functional_trims.amethyst.stand",
                () -> cfg.amethyst.motionlessSecondsBeforeEffectStanding, 3.0f, 0.0f, 60.0f,
                val -> cfg.amethyst.motionlessSecondsBeforeEffectStanding = val));
        entries.add(floatEntry(entry, "config.functional_trims.amethyst.sneak",
                () -> cfg.amethyst.motionlessSecondsBeforeEffectSneaking, 1.5f, 0.0f, 60.0f,
                val -> cfg.amethyst.motionlessSecondsBeforeEffectSneaking = val));
        entries.add(floatEntry(entry, "config.functional_trims.amethyst.range",
                () -> cfg.amethyst.effectRangeMultiplier, 1.0f, 0.1f, 10.0f,
                val -> cfg.amethyst.effectRangeMultiplier = val));

        category.addEntry(subCategory(entry, "config.functional_trims.amethyst", "config.functional_trims.amethyst.tooltip"));
    }

    private static void addCopper(ConfigEntryBuilder entry, ConfigCategory category, FunctionalTrimsConfig cfg) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        addEnabledToggle(entry, entries, cfg.copper);
        entries.add(floatEntry(entry, "config.functional_trims.copper.lightning",
                () -> cfg.copper.lightningStrikeChanceMultiplier, 1.0f, 0.0f, 10.0f,
                val -> cfg.copper.lightningStrikeChanceMultiplier = val));
        entries.add(floatEntry(entry, "config.functional_trims.copper.charged_time",
                () -> cfg.copper.chargedEffectDuration, 60.0f, 0.0f, 600.0f,
                val -> cfg.copper.chargedEffectDuration = val));
        entries.add(floatEntry(entry, "config.functional_trims.copper.charged_damage",
                () -> cfg.copper.chargedStrikeDamageMultiplier, 1.5f, 0.0f, 10.0f,
                val -> cfg.copper.chargedStrikeDamageMultiplier = val));

        category.addEntry(subCategory(entry, "config.functional_trims.copper", "config.functional_trims.copper.tooltip"));
    }

    private static void addResin(ConfigEntryBuilder entry, ConfigCategory category, FunctionalTrimsConfig cfg) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        addEnabledToggle(entry, entries, cfg.resin);
        entries.add(floatEntry(entry, "config.functional_trims.resin.grip",
                () -> cfg.resin.gripStrengthMultiplier, 1.0f, 0.1f, 3.0f,
                val -> cfg.resin.gripStrengthMultiplier = val));

        category.addEntry(subCategory(entry, "config.functional_trims.resin", "config.functional_trims.resin.tooltip"));
    }

    private static void addEnabledToggle(
            ConfigEntryBuilder entry,
            List<AbstractConfigListEntry<?>> entries,
            FunctionalTrimsConfig.TrimSection section
    ) {
        entries.add(boolEntry(
                entry,
                "config.functional_trims.enabled",
                null,
                () -> section.enabled,
                val -> section.enabled = val
        ));
    }

    private static AbstractConfigListEntry<?> subCategory(
            ConfigEntryBuilder entry,
            String titleKey,
            String tooltipKey
    ) {
        var sub = entry.startSubCategory(Text.translatable(titleKey));
        if (tooltipKey != null) {
            sub.setTooltip(Text.translatable(tooltipKey));
        }
        return sub.build();
    }

    private static AbstractConfigListEntry<?> boolEntry(
            ConfigEntryBuilder entry,
            String titleKey,
            String tooltipKey,
            Supplier<Boolean> getter,
            Consumer<Boolean> saver
    ) {
        var field = entry.startBooleanToggle(Text.translatable(titleKey), getter.get())
                .setDefaultValue(true)
                .setSaveConsumer(saver);

        if (tooltipKey != null) {
            field.setTooltip(Text.translatable(tooltipKey));
        }

        return field.build();
    }

    private static AbstractConfigListEntry<?> intEntry(
            ConfigEntryBuilder entry,
            Supplier<Integer> getter,
            Consumer<Integer> saver
    ) {
        var field = entry.startIntField(Text.translatable("config.functional_trims.redstone.power"), getter.get())
                .setDefaultValue(15)
                .setMin(0)
                .setMax(15)
                .setSaveConsumer(saver);

        return field.build();
    }

    private static AbstractConfigListEntry<?> floatEntry(
            ConfigEntryBuilder entry,
            String titleKey,
            Supplier<Float> getter,
            float defaultValue,
            float min,
            float max,
            Consumer<Float> saver
    ) {
        var field = entry.startFloatField(Text.translatable(titleKey), getter.get())
                .setDefaultValue(defaultValue)
                .setMin(min)
                .setMax(max)
                .setSaveConsumer(saver);

        return field.build();
    }
}