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
                .setTitle(Text.literal("Functional Trims"));

        builder.setSavingRunnable(ConfigManager::save);

        ConfigEntryBuilder entry = builder.entryBuilder();

        ConfigCategory materials = builder.getOrCreateCategory(Text.literal("Trim Materials"));

        // Master toggle
        materials.addEntry(
                entry.startBooleanToggle(Text.literal("Enable All Trim Effects"), cfg.enableAll)
                        .setDefaultValue(true)
                        .setTooltip(Text.literal("Disables all trim effects globally"))
                        .setSaveConsumer(val -> cfg.enableAll = val)
                        .build()
        );

        // ----------------
        // Iron
        // ----------------
        materials.addEntry(
                entry.startSubCategory(
                        Text.literal("Iron"),
                        List.of(
                                entry.startBooleanToggle(Text.literal("Enabled"), cfg.ironEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.ironEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Projectile Reflect Chance (0-1)"), cfg.projectileReflectChance)
                                        .setDefaultValue(0.6f)
                                        .setMin(0.0f)
                                        .setMax(1.0f)
                                        .setSaveConsumer(val -> cfg.projectileReflectChance = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Shield Knockback Strength Multiplier"), cfg.shieldKnockbackStrengthMultiplier)
                                        .setDefaultValue(1.15f)
                                        .setMin(0.0f)
                                        .setMax(5.0f)
                                        .setSaveConsumer(val -> cfg.shieldKnockbackStrengthMultiplier = val)
                                        .build(),

                                entry.startBooleanToggle(Text.literal("Axe Attack Resistance"), cfg.axeAttackResistanceEnabled)
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
                        Text.literal("Gold"),
                        List.of(
                                entry.startBooleanToggle(Text.literal("Enabled"), cfg.goldEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.goldEnabled = val)
                                        .build(),

                                entry.startBooleanToggle(Text.literal("Distract Piglin Brutes"), cfg.distractPiglinBrutesEnabled)
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
                        Text.literal("Diamond"),
                        List.of(
                                entry.startBooleanToggle(Text.literal("Enabled"), cfg.diamondEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.diamondEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Health Regained After Burst (0-1)"), cfg.percentHealthRegainedAfterBurst)
                                        .setDefaultValue(0.5f)
                                        .setMin(0.0f)
                                        .setMax(1.0f)
                                        .setSaveConsumer(val -> cfg.percentHealthRegainedAfterBurst = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Armor Durability Lost After Burst (0-1)"), cfg.percentArmorDurabilityLostAfterBurst)
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
                        Text.literal("Netherite"),
                        List.of(
                                entry.startBooleanToggle(Text.literal("Enabled"), cfg.netheriteEnabled)
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
                        Text.literal("Redstone"),
                        List.of(
                                entry.startBooleanToggle(Text.literal("Enabled"), cfg.redstoneEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.redstoneEnabled = val)
                                        .build(),

                                entry.startIntField(Text.literal("Block Power Level When Stepped On (0-15)"), cfg.blockPowerLevelWhenSteppedOn)
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
                        Text.literal("Emerald"),
                        List.of(
                                entry.startBooleanToggle(Text.literal("Enabled"), cfg.emeraldEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.emeraldEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Extra Roll #1 Chance (0-1)"), cfg.percentChanceForExtraRoll1)
                                        .setDefaultValue(1.0f)
                                        .setMin(0.0f)
                                        .setMax(1.0f)
                                        .setSaveConsumer(val -> cfg.percentChanceForExtraRoll1 = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Extra Roll #2 Chance (0-1)"), cfg.percentChanceForExtraRoll2)
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
                        Text.literal("Lapis"),
                        List.of(
                                entry.startBooleanToggle(Text.literal("Enabled"), cfg.lapisEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.lapisEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Extra EXP Multiplier"), cfg.extraExpMultiplier)
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
                        Text.literal("Quartz"),
                        List.of(
                                entry.startBooleanToggle(Text.literal("Enabled"), cfg.quartzEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.quartzEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Hunger Restored Multiplier"), cfg.hungerRestoredMultiplier)
                                        .setDefaultValue(1.25f)
                                        .setMin(0.0f)
                                        .setMax(10.0f)
                                        .setSaveConsumer(val -> cfg.hungerRestoredMultiplier = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Saturation Restored Multiplier"), cfg.saturationRestoredMultiplier)
                                        .setDefaultValue(1.25f)
                                        .setMin(0.0f)
                                        .setMax(10.0f)
                                        .setSaveConsumer(val -> cfg.saturationRestoredMultiplier = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Potion Effect Duration Multiplier"), cfg.potionEffectDurationMultiplier)
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
                        Text.literal("Amethyst"),
                        List.of(
                                entry.startBooleanToggle(Text.literal("Enabled"), cfg.amethystEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.amethystEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Seconds Standing Still Before Effect"), cfg.motionlessSecondsBeforeEffectStanding)
                                        .setDefaultValue(3.0f)
                                        .setMin(0.0f)
                                        .setMax(60.0f)
                                        .setSaveConsumer(val -> cfg.motionlessSecondsBeforeEffectStanding = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Seconds Sneaking Still Before Effect"), cfg.motionlessSecondsBeforeEffectSneaking)
                                        .setDefaultValue(1.5f)
                                        .setMin(0.0f)
                                        .setMax(60.0f)
                                        .setSaveConsumer(val -> cfg.motionlessSecondsBeforeEffectSneaking = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Effect Range Multiplier"), cfg.effectRangeMultiplier)
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
                        Text.literal("Copper"),
                        List.of(
                                entry.startBooleanToggle(Text.literal("Enabled"), cfg.copperEnabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(val -> cfg.copperEnabled = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Lightning Strike Chance Multiplier"), cfg.lightningStrikeChanceMultiplier)
                                        .setDefaultValue(1.0f)
                                        .setMin(0.0f)
                                        .setMax(10.0f)
                                        .setSaveConsumer(val -> cfg.lightningStrikeChanceMultiplier = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Charged Effect Duration (seconds)"), cfg.chargedEffectDuration)
                                        .setDefaultValue(60.0f)
                                        .setMin(0.0f)
                                        .setMax(600.0f)
                                        .setSaveConsumer(val -> cfg.chargedEffectDuration = val)
                                        .build(),

                                entry.startFloatField(Text.literal("Charged Strike Damage Multiplier"), cfg.chargedStrikeDamageMultiplier)
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