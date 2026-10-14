package functional_trims.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public final class FunctionalTrimsConfigScreen {
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

        // Master toggle
        materials.addEntry(
                entry.startBooleanToggle(
                                Text.translatable("config.functional_trims.enable_all"),
                                cfg.modEnabled
                        )
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable("config.functional_trims.enable_all.tooltip"))
                        .setSaveConsumer(val -> cfg.modEnabled = val)
                        .build()
        );

        // Iron
        materials.addEntry(
                entry.startSubCategory(
                                Text.translatable("config.functional_trims.iron"),
                                List.of(
                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.enabled"),
                                                        cfg.iron.enabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.iron.enabled = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.iron.reflect"),
                                                        cfg.iron.projectileReflectChance
                                                )
                                                .setDefaultValue(0.6f)
                                                .setMin(0.0f)
                                                .setMax(1.0f)
                                                .setSaveConsumer(val -> cfg.iron.projectileReflectChance = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.iron.knockback"),
                                                        cfg.iron.shieldKnockbackStrengthMultiplier
                                                )
                                                .setDefaultValue(1.15f)
                                                .setMin(0.0f)
                                                .setMax(5.0f)
                                                .setSaveConsumer(val -> cfg.iron.shieldKnockbackStrengthMultiplier = val)
                                                .build(),

                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.iron.axe_resist"),
                                                        cfg.iron.axeAttackResistanceEnabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.iron.axeAttackResistanceEnabled = val)
                                                .build()
                                )
                        )
                        .setTooltip(Text.translatable("config.functional_trims.iron.tooltip"))
                        .build()
        );

        // Gold
        materials.addEntry(
                entry.startSubCategory(
                                Text.translatable("config.functional_trims.gold"),
                                List.of(
                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.enabled"),
                                                        cfg.gold.enabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.gold.enabled = val)
                                                .build(),

                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.gold.distract"),
                                                        cfg.gold.distractPiglinBrutesEnabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.gold.distractPiglinBrutesEnabled = val)
                                                .build()
                                )
                        )
                        .setTooltip(Text.translatable("config.functional_trims.gold.tooltip"))
                        .build()
        );

        // Diamond
        materials.addEntry(
                entry.startSubCategory(
                                Text.translatable("config.functional_trims.diamond"),
                                List.of(
                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.enabled"),
                                                        cfg.diamond.enabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.diamond.enabled = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.diamond.heal"),
                                                        cfg.diamond.percentHealthRegainedAfterBurst
                                                )
                                                .setDefaultValue(0.5f)
                                                .setMin(0.0f)
                                                .setMax(1.0f)
                                                .setSaveConsumer(val -> cfg.diamond.percentHealthRegainedAfterBurst = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.diamond.armor_loss"),
                                                        cfg.diamond.percentArmorDurabilityLostAfterBurst
                                                )
                                                .setDefaultValue(1.0f)
                                                .setMin(0.0f)
                                                .setMax(1.0f)
                                                .setSaveConsumer(val -> cfg.diamond.percentArmorDurabilityLostAfterBurst = val)
                                                .build()
                                )
                        )
                        .setTooltip(Text.translatable("config.functional_trims.diamond.tooltip"))
                        .build()
        );

        // Netherite
        materials.addEntry(
                entry.startSubCategory(
                                Text.translatable("config.functional_trims.netherite"),
                                List.of(
                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.enabled"),
                                                        cfg.netherite.enabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.netherite.enabled = val)
                                                .build()
                                )
                        )
                        .setTooltip(Text.translatable("config.functional_trims.netherite.tooltip"))
                        .build()
        );

        // Redstone
        materials.addEntry(
                entry.startSubCategory(
                                Text.translatable("config.functional_trims.redstone"),
                                List.of(
                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.enabled"),
                                                        cfg.redstone.enabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.redstone.enabled = val)
                                                .build(),

                                        entry.startIntField(
                                                        Text.translatable("config.functional_trims.redstone.power"),
                                                        cfg.redstone.blockPowerLevelWhenSteppedOn
                                                )
                                                .setDefaultValue(15)
                                                .setMin(0)
                                                .setMax(15)
                                                .setSaveConsumer(val -> cfg.redstone.blockPowerLevelWhenSteppedOn = val)
                                                .build()
                                )
                        )
                        .setTooltip(Text.translatable("config.functional_trims.redstone.tooltip"))
                        .build()
        );

        // Emerald
        materials.addEntry(
                entry.startSubCategory(
                                Text.translatable("config.functional_trims.emerald"),
                                List.of(
                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.enabled"),
                                                        cfg.emerald.enabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.emerald.enabled = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.emerald.roll1"),
                                                        cfg.emerald.percentChanceForExtraRoll1
                                                )
                                                .setDefaultValue(1.0f)
                                                .setMin(0.0f)
                                                .setMax(1.0f)
                                                .setSaveConsumer(val -> cfg.emerald.percentChanceForExtraRoll1 = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.emerald.roll2"),
                                                        cfg.emerald.percentChanceForExtraRoll2
                                                )
                                                .setDefaultValue(0.5f)
                                                .setMin(0.0f)
                                                .setMax(1.0f)
                                                .setSaveConsumer(val -> cfg.emerald.percentChanceForExtraRoll2 = val)
                                                .build()
                                )
                        )
                        .setTooltip(Text.translatable("config.functional_trims.emerald.tooltip"))
                        .build()
        );

        // Lapis
        materials.addEntry(
                entry.startSubCategory(
                                Text.translatable("config.functional_trims.lapis"),
                                List.of(
                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.enabled"),
                                                        cfg.lapis.enabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.lapis.enabled = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.lapis.exp"),
                                                        cfg.lapis.extraExpMultiplier
                                                )
                                                .setDefaultValue(0.5f)
                                                .setMin(0.0f)
                                                .setMax(10.0f)
                                                .setSaveConsumer(val -> cfg.lapis.extraExpMultiplier = val)
                                                .build()
                                )
                        )
                        .setTooltip(Text.translatable("config.functional_trims.lapis.tooltip"))
                        .build()
        );

        // Quartz
        materials.addEntry(
                entry.startSubCategory(
                                Text.translatable("config.functional_trims.quartz"),
                                List.of(
                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.enabled"),
                                                        cfg.quartz.enabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.quartz.enabled = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.quartz.hunger"),
                                                        cfg.quartz.hungerRestoredMultiplier
                                                )
                                                .setDefaultValue(1.25f)
                                                .setMin(0.0f)
                                                .setMax(10.0f)
                                                .setSaveConsumer(val -> cfg.quartz.hungerRestoredMultiplier = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.quartz.saturation"),
                                                        cfg.quartz.saturationRestoredMultiplier
                                                )
                                                .setDefaultValue(1.25f)
                                                .setMin(0.0f)
                                                .setMax(10.0f)
                                                .setSaveConsumer(val -> cfg.quartz.saturationRestoredMultiplier = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.quartz.potion"),
                                                        cfg.quartz.potionEffectDurationMultiplier
                                                )
                                                .setDefaultValue(1.25f)
                                                .setMin(0.0f)
                                                .setMax(10.0f)
                                                .setSaveConsumer(val -> cfg.quartz.potionEffectDurationMultiplier = val)
                                                .build()
                                )
                        )
                        .setTooltip(Text.translatable("config.functional_trims.quartz.tooltip"))
                        .build()
        );

        // Amethyst
        materials.addEntry(
                entry.startSubCategory(
                                Text.translatable("config.functional_trims.amethyst"),
                                List.of(
                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.enabled"),
                                                        cfg.amethyst.enabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.amethyst.enabled = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.amethyst.stand"),
                                                        cfg.amethyst.motionlessSecondsBeforeEffectStanding
                                                )
                                                .setDefaultValue(3.0f)
                                                .setMin(0.0f)
                                                .setMax(60.0f)
                                                .setSaveConsumer(val -> cfg.amethyst.motionlessSecondsBeforeEffectStanding = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.amethyst.sneak"),
                                                        cfg.amethyst.motionlessSecondsBeforeEffectSneaking
                                                )
                                                .setDefaultValue(1.5f)
                                                .setMin(0.0f)
                                                .setMax(60.0f)
                                                .setSaveConsumer(val -> cfg.amethyst.motionlessSecondsBeforeEffectSneaking = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.amethyst.range"),
                                                        cfg.amethyst.effectRangeMultiplier
                                                )
                                                .setDefaultValue(1.0f)
                                                .setMin(0.1f)
                                                .setMax(10.0f)
                                                .setSaveConsumer(val -> cfg.amethyst.effectRangeMultiplier = val)
                                                .build()
                                )
                        )
                        .setTooltip(Text.translatable("config.functional_trims.amethyst.tooltip"))
                        .build()
        );

        // Copper
        materials.addEntry(
                entry.startSubCategory(
                                Text.translatable("config.functional_trims.copper"),
                                List.of(
                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.enabled"),
                                                        cfg.copper.enabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.copper.enabled = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.copper.lightning"),
                                                        cfg.copper.lightningStrikeChanceMultiplier
                                                )
                                                .setDefaultValue(1.0f)
                                                .setMin(0.0f)
                                                .setMax(10.0f)
                                                .setSaveConsumer(val -> cfg.copper.lightningStrikeChanceMultiplier = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.copper.charged_time"),
                                                        cfg.copper.chargedEffectDuration
                                                )
                                                .setDefaultValue(60.0f)
                                                .setMin(0.0f)
                                                .setMax(600.0f)
                                                .setSaveConsumer(val -> cfg.copper.chargedEffectDuration = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.copper.charged_damage"),
                                                        cfg.copper.chargedStrikeDamageMultiplier
                                                )
                                                .setDefaultValue(1.5f)
                                                .setMin(0.0f)
                                                .setMax(10.0f)
                                                .setSaveConsumer(val -> cfg.copper.chargedStrikeDamageMultiplier = val)
                                                .build()
                                )
                        )
                        .setTooltip(Text.translatable("config.functional_trims.copper.tooltip"))
                        .build()
        );

        // Resin
        materials.addEntry(
                entry.startSubCategory(
                                Text.translatable("config.functional_trims.resin"),
                                List.of(
                                        entry.startBooleanToggle(
                                                        Text.translatable("config.functional_trims.enabled"),
                                                        cfg.resin.enabled
                                                )
                                                .setDefaultValue(true)
                                                .setSaveConsumer(val -> cfg.resin.enabled = val)
                                                .build(),

                                        entry.startFloatField(
                                                        Text.translatable("config.functional_trims.resin.grip"),
                                                        cfg.resin.gripStrengthMultiplier
                                                )
                                                .setDefaultValue(1.0f)
                                                .setMin(0.1f)
                                                .setMax(3.0f)
                                                .setSaveConsumer(val -> cfg.resin.gripStrengthMultiplier = val)
                                                .build()
                                )
                        )
                        .setTooltip(Text.translatable("config.functional_trims.resin.tooltip"))
                        .build()
        );

        return builder.build();
    }
}