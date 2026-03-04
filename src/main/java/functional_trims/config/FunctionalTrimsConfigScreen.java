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

        ConfigCategory materials = builder.getOrCreateCategory(Text.translatable("config.functional_trims.category.trim_materials"));

        // Master toggle
        materials.addEntry(
                entry.startBooleanToggle(Text.translatable("config.functional_trims.enable_all"), cfg.enableAll)
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable("config.functional_trims.enable_all.tooltip"))
                        .setSaveConsumer(val -> cfg.enableAll = val)
                        .build()
        );

        // ----------------
        // Iron
        // ----------------
        materials.addEntry(
                entry.startSubCategory(
                        Text.translatable("config.functional_trims.iron"),
                        List.of(
                                entry.startBooleanToggle(Text.translatable("config.functional_trims.enabled"), cfg.ironEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.ironEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.iron.reflect"), cfg.projectileReflectChance)
                                        .setDefaultValue(0.6f)
                                        .setMin(0.0f)
                                        .setMax(1.0f)
                                        .setSaveConsumer(val -> cfg.projectileReflectChance = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.iron.knockback"), cfg.shieldKnockbackStrengthMultiplier)
                                        .setDefaultValue(1.15f)
                                        .setMin(0.0f)
                                        .setMax(5.0f)
                                        .setSaveConsumer(val -> cfg.shieldKnockbackStrengthMultiplier = val)
                                        .build(),

                                entry.startBooleanToggle(Text.translatable("config.functional_trims.iron.axe_resist"), cfg.axeAttackResistanceEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.axeAttackResistanceEnabled = val)
                                        .build()
                        )
                ).build()
        );

        // ----------------
        // Gold
        // ----------------
        materials.addEntry(
                entry.startSubCategory(
                        Text.translatable("config.functional_trims.gold"),
                        List.of(
                                entry.startBooleanToggle(Text.translatable("config.functional_trims.enabled"), cfg.goldEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.goldEnabled = val)
                                        .build(),

                                entry.startBooleanToggle(Text.translatable("config.functional_trims.gold.distract"), cfg.distractPiglinBrutesEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.distractPiglinBrutesEnabled = val)
                                        .build()
                        )
                ).build()
        );

        // ----------------
        // Diamond
        // ----------------
        materials.addEntry(
                entry.startSubCategory(
                        Text.translatable("config.functional_trims.diamond"),
                        List.of(
                                entry.startBooleanToggle(Text.translatable("config.functional_trims.enabled"), cfg.diamondEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.diamondEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.diamond.heal_burst"), cfg.percentHealthRegainedAfterBurst)
                                        .setDefaultValue(0.5f)
                                        .setMin(0.0f)
                                        .setMax(1.0f)
                                        .setSaveConsumer(val -> cfg.percentHealthRegainedAfterBurst = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.diamond.armor_loss"), cfg.percentArmorDurabilityLostAfterBurst)
                                        .setDefaultValue(1.0f)
                                        .setMin(0.0f)
                                        .setMax(1.0f)
                                        .setSaveConsumer(val -> cfg.percentArmorDurabilityLostAfterBurst = val)
                                        .build()
                        )
                ).build()
        );

        // ----------------
        // Netherite
        // ----------------
        materials.addEntry(
                entry.startSubCategory(
                        Text.translatable("config.functional_trims.netherite"),
                        List.of(
                                entry.startBooleanToggle(Text.translatable("config.functional_trims.enabled"), cfg.netheriteEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.netheriteEnabled = val)
                                        .build()
                        )
                ).build()
        );

        // ----------------
        // Redstone
        // ----------------
        materials.addEntry(
                entry.startSubCategory(
                        Text.translatable("config.functional_trims.redstone"),
                        List.of(
                                entry.startBooleanToggle(Text.translatable("config.functional_trims.enabled"), cfg.redstoneEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.redstoneEnabled = val)
                                        .build(),

                                entry.startIntField(Text.translatable("config.functional_trims.redstone.power"), cfg.blockPowerLevelWhenSteppedOn)
                                        .setDefaultValue(15)
                                        .setMin(0)
                                        .setMax(15)
                                        .setSaveConsumer(val -> cfg.blockPowerLevelWhenSteppedOn = val)
                                        .build()
                        )
                ).build()
        );

        // ----------------
        // Emerald
        // ----------------
        materials.addEntry(
                entry.startSubCategory(
                        Text.translatable("config.functional_trims.emerald"),
                        List.of(
                                entry.startBooleanToggle(Text.translatable("config.functional_trims.enabled"), cfg.emeraldEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.emeraldEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.emerald.roll1"), cfg.percentChanceForExtraRoll1)
                                        .setDefaultValue(1.0f)
                                        .setMin(0.0f)
                                        .setMax(1.0f)
                                        .setSaveConsumer(val -> cfg.percentChanceForExtraRoll1 = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.emerald.roll2"), cfg.percentChanceForExtraRoll2)
                                        .setDefaultValue(0.5f)
                                        .setMin(0.0f)
                                        .setMax(1.0f)
                                        .setSaveConsumer(val -> cfg.percentChanceForExtraRoll2 = val)
                                        .build()
                        )
                ).build()
        );

        // ----------------
        // Lapis
        // ----------------
        materials.addEntry(
                entry.startSubCategory(
                        Text.translatable("config.functional_trims.lapis"),
                        List.of(
                                entry.startBooleanToggle(Text.translatable("config.functional_trims.enabled"), cfg.lapisEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.lapisEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.lapis.exp"), cfg.extraExpMultiplier)
                                        .setDefaultValue(0.5f)
                                        .setMin(0.0f)
                                        .setMax(10.0f)
                                        .setSaveConsumer(val -> cfg.extraExpMultiplier = val)
                                        .build()
                        )
                ).build()
        );

        // ----------------
        // Quartz
        // ----------------
        materials.addEntry(
                entry.startSubCategory(
                        Text.translatable("config.functional_trims.quartz"),
                        List.of(
                                entry.startBooleanToggle(Text.translatable("config.functional_trims.enabled"), cfg.quartzEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.quartzEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.quartz.hunger"), cfg.hungerRestoredMultiplier)
                                        .setDefaultValue(1.25f)
                                        .setMin(0.0f)
                                        .setMax(10.0f)
                                        .setSaveConsumer(val -> cfg.hungerRestoredMultiplier = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.quartz.saturation"), cfg.saturationRestoredMultiplier)
                                        .setDefaultValue(1.25f)
                                        .setMin(0.0f)
                                        .setMax(10.0f)
                                        .setSaveConsumer(val -> cfg.saturationRestoredMultiplier = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.quartz.potion"), cfg.potionEffectDurationMultiplier)
                                        .setDefaultValue(1.25f)
                                        .setMin(0.0f)
                                        .setMax(10.0f)
                                        .setSaveConsumer(val -> cfg.potionEffectDurationMultiplier = val)
                                        .build()
                        )
                ).build()
        );

        // ----------------
        // Amethyst
        // ----------------
        materials.addEntry(
                entry.startSubCategory(
                        Text.translatable("config.functional_trims.amethyst"),
                        List.of(
                                entry.startBooleanToggle(Text.translatable("config.functional_trims.enabled"), cfg.amethystEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.amethystEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.amethyst.stand"), cfg.motionlessSecondsBeforeEffectStanding)
                                        .setDefaultValue(3.0f)
                                        .setMin(0.0f)
                                        .setMax(60.0f)
                                        .setSaveConsumer(val -> cfg.motionlessSecondsBeforeEffectStanding = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.amethyst.sneak"), cfg.motionlessSecondsBeforeEffectSneaking)
                                        .setDefaultValue(1.5f)
                                        .setMin(0.0f)
                                        .setMax(60.0f)
                                        .setSaveConsumer(val -> cfg.motionlessSecondsBeforeEffectSneaking = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.amethyst.range"), cfg.effectRangeMultiplier)
                                        .setDefaultValue(1.0f)
                                        .setMin(0.1f)
                                        .setMax(10.0f)
                                        .setSaveConsumer(val -> cfg.effectRangeMultiplier = val)
                                        .build()
                        )
                ).build()
        );

        // ----------------
        // Copper
        // ----------------
        materials.addEntry(
                entry.startSubCategory(
                        Text.translatable("config.functional_trims.copper"),
                        List.of(
                                entry.startBooleanToggle(Text.translatable("config.functional_trims.enabled"), cfg.copperEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.copperEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.copper.lightning"), cfg.lightningStrikeChanceMultiplier)
                                        .setDefaultValue(1.0f)
                                        .setMin(0.0f)
                                        .setMax(10.0f)
                                        .setSaveConsumer(val -> cfg.lightningStrikeChanceMultiplier = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.copper.charged_time"), cfg.chargedEffectDuration)
                                        .setDefaultValue(60.0f)
                                        .setMin(0.0f)
                                        .setMax(600.0f)
                                        .setSaveConsumer(val -> cfg.chargedEffectDuration = val)
                                        .build(),

                                entry.startFloatField(Text.translatable("config.functional_trims.copper.charged_damage"), cfg.chargedStrikeDamageMultiplier)
                                        .setDefaultValue(1.5f)
                                        .setMin(0.0f)
                                        .setMax(10.0f)
                                        .setSaveConsumer(val -> cfg.chargedStrikeDamageMultiplier = val)
                                        .build()
                        )
                ).build()
        );
        return builder.build();
    }
}
