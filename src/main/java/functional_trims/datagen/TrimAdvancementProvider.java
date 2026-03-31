package functional_trims.datagen;

import functional_trims.criteria.ModCriteria;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import functional_trims.FunctionalTrims;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TrimAdvancementProvider extends FabricAdvancementProvider {
    public TrimAdvancementProvider(FabricPackOutput output,
                                   CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generateAdvancement(
        @NotNull HolderLookup.Provider registries,
        @NotNull Consumer<AdvancementHolder> consumer) {

        // Root
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        Items.SMITHING_TABLE,
                        Component.translatable("advancements.functional_trims.root.title"),
                        Component.translatable("advancements.functional_trims.root.description"),
                        Identifier.fromNamespaceAndPath("minecraft", "block/polished_blackstone_bricks"),
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":root");

        // Full redstone trim advancement
        AdvancementHolder powerWalk = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.REDSTONE,
                        Component.translatable("advancements.functional_trims.full_redstone_trim.title"),
                        Component.translatable("advancements.functional_trims.full_redstone_trim.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":full_redstone_trim");

        // Redstone trim sub-advancement
        Advancement.Builder.advancement()
                .parent(powerWalk)
                .display(
                        Items.REDSTONE_LAMP,
                        Component.translatable("advancements.functional_trims.redstone.redstone_lamp_activation.title"),
                        Component.translatable("advancements.functional_trims.redstone.redstone_lamp_activation.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("activate_lamp",
                        ModCriteria.TRIM_TRIGGER.criterion("redstone", "activate_lamp"))
                .save(consumer, FunctionalTrims.MOD_ID + ":redstone_lamp_activation");


        // Full emerald trim advancement
        AdvancementHolder explorersFortune = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.EMERALD,
                        Component.translatable("advancements.functional_trims.full_emerald_trim.title"),
                        Component.translatable("advancements.functional_trims.full_emerald_trim.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":full_emerald_trim");

        // Emerald trim sub-advancement
        Advancement.Builder.advancement()
                .parent(explorersFortune)
                .display(
                        Items.CHEST,
                        Component.translatable("advancements.functional_trims.emerald.1_in_7_5_trillion.title"),
                        Component.translatable("advancements.functional_trims.emerald.1_in_7_5_trillion.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("open_chest",
                        ModCriteria.TRIM_TRIGGER.criterion("emerald", "open_loot_chest"))
                .save(consumer, FunctionalTrims.MOD_ID + ":emerald/1_in_7.5_trillion");


        // Full lapis trim advancement
        AdvancementHolder scholarsInsight = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.LAPIS_LAZULI,
                        Component.translatable("advancements.functional_trims.full_lapis_trim.title"),
                        Component.translatable("advancements.functional_trims.full_lapis_trim.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":full_lapis_trim");

        // Lapis trim sub-advancement
        Advancement.Builder.advancement()
                .parent(scholarsInsight)
                .display(
                        Items.ENDER_EYE,
                        Component.translatable("advancements.functional_trims.lapis.level_100.title"),
                        Component.translatable("advancements.functional_trims.lapis.level_100.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true, true, true
                )
                .addCriterion("reach_level_100",
                        ModCriteria.TRIM_TRIGGER.criterion("lapis", "reach_level_100"))
                .save(consumer, FunctionalTrims.MOD_ID + ":lapis/level_100");

        Advancement.Builder.advancement()
                .parent(scholarsInsight)
                .display(
                        Items.EXPERIENCE_BOTTLE,
                        Component.translatable("advancements.functional_trims.lapis.first_lesson.title"),
                        Component.translatable("advancements.functional_trims.lapis.first_lesson.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("absorb_xp_orb",
                        ModCriteria.TRIM_TRIGGER.criterion("lapis", "absorb_xp_orb"))
                .save(consumer, FunctionalTrims.MOD_ID + ":lapis/first_lesson");

        // Full gold trim advancement
        AdvancementHolder lordOfTheNether = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.GOLD_INGOT,
                        Component.translatable("advancements.functional_trims.full_gold_trim.title"),
                        Component.translatable("advancements.functional_trims.full_gold_trim.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":full_gold_trim");

        // Gold trim sub-advancement
        Advancement.Builder.advancement()
                .parent(lordOfTheNether)
                .display(
                        Items.GILDED_BLACKSTONE,
                        Component.translatable("advancements.functional_trims.gold.enter_bastion.title"),
                        Component.translatable("advancements.functional_trims.gold.enter_bastion.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("enter_bastion",
                        ModCriteria.TRIM_TRIGGER.criterion("gold", "enter_bastion"))
                .save(consumer, FunctionalTrims.MOD_ID + ":gold/enter_bastion");

        // Gold trim sub-advancement
        Advancement.Builder.advancement()
                .parent(lordOfTheNether)
                .display(
                        Items.GOLDEN_SWORD,
                        Component.translatable("advancements.functional_trims.gold.attack_piglin_brute.title"),
                        Component.translatable("advancements.functional_trims.gold.attack_piglin_brute.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("attack_piglin_brute",
                        ModCriteria.TRIM_TRIGGER.criterion("gold", "attack_piglin_brute"))
                .save(consumer, FunctionalTrims.MOD_ID + ":gold/attack_piglin_brute");

        // Full diamond trim advancement
        AdvancementHolder shatteringResilliance = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.DIAMOND,
                        Component.translatable("advancements.functional_trims.full_diamond_trim.title"),
                        Component.translatable("advancements.functional_trims.full_diamond_trim.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":full_diamond_trim");

        // Diamond trim sub-advancement
        Advancement.Builder.advancement()
                .parent(shatteringResilliance)
                .display(
                        Items.NETHER_STAR,
                        Component.translatable("advancements.functional_trims.diamond.armor_shatter.title"),
                        Component.translatable("advancements.functional_trims.diamond.armor_shatter.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("attack_piglin_brute",
                        ModCriteria.TRIM_TRIGGER.criterion("diamond", "armor_shatter"))
                .save(consumer, FunctionalTrims.MOD_ID + ":diamond/armor_shatter");

        // Full netherite trim advancement
        AdvancementHolder immoveableObject = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.NETHERITE_INGOT,
                        Component.translatable("advancements.functional_trims.full_netherite_trim.title"),
                        Component.translatable("advancements.functional_trims.full_netherite_trim.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":full_netherite_trim");

        // Netherite trim sub-advancement
        Advancement.Builder.advancement()
                .parent(immoveableObject)
                .display(
                        Items.TNT,
                        Component.translatable("advancements.functional_trims.netherite.that_was_cute.title"),
                        Component.translatable("advancements.functional_trims.netherite.that_was_cute.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("resist_explosion",
                        ModCriteria.TRIM_TRIGGER.criterion("netherite", "resist_explosion"))
                .save(consumer, FunctionalTrims.MOD_ID + ":netherite/that_was_cute");

        // Full iron trim advancement
        AdvancementHolder unyieldingDefense = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.IRON_INGOT,
                        Component.translatable("advancements.functional_trims.full_iron_trim.title"),
                        Component.translatable("advancements.functional_trims.full_iron_trim.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":full_iron_trim");

        // Iron trim sub-advancement
        Advancement.Builder.advancement()
                .parent(unyieldingDefense)
                .display(
                        Items.ARROW,
                        Component.translatable("advancements.functional_trims.iron.reflect_projectile.title"),
                        Component.translatable("advancements.functional_trims.iron.reflect_projectile.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("reflect_projectile",
                        ModCriteria.TRIM_TRIGGER.criterion("iron", "reflect_projectile"))
                .save(consumer, FunctionalTrims.MOD_ID + ":iron/reflect_projectile");

        // Iron trim sub-advancement
        Advancement.Builder.advancement()
                .parent(unyieldingDefense)
                .display(
                        Items.SHIELD,
                        Component.translatable("advancements.functional_trims.iron.knockback_attacker.title"),
                        Component.translatable("advancements.functional_trims.iron.knockback_attacker.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("knockback_attacker",
                        ModCriteria.TRIM_TRIGGER.criterion("iron", "knockback_attacker"))
                .save(consumer, FunctionalTrims.MOD_ID + ":iron/knockback_attacker");

        // Full copper trim advancement
        AdvancementHolder superchargedStrike = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.COPPER_INGOT,
                        Component.translatable("advancements.functional_trims.full_copper_trim.title"),
                        Component.translatable("advancements.functional_trims.full_copper_trim.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":full_copper_trim");

        // Copper trim sub-advancement
        Advancement.Builder.advancement()
                .parent(superchargedStrike)
                .display(
                        Items.LIGHTNING_ROD,
                        Component.translatable("advancements.functional_trims.copper.struck_by_lightning.title"),
                        Component.translatable("advancements.functional_trims.copper.struck_by_lightning.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("struck_by_lightning",
                        ModCriteria.TRIM_TRIGGER.criterion("copper", "struck_by_lightning"))
                .save(consumer, FunctionalTrims.MOD_ID + ":copper/struck_by_lightning");

        // Copper trim sub-advancement
        Advancement.Builder.advancement()
                .parent(superchargedStrike)
                .display(
                        Items.MACE,
                        Component.translatable("advancements.functional_trims.copper.mace_strike.title"),
                        Component.translatable("advancements.functional_trims.copper.mace_strike.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true, true, false
                )
                .addCriterion("mace_strike",
                        ModCriteria.TRIM_TRIGGER.criterion("copper", "mace_strike"))
                .save(consumer, FunctionalTrims.MOD_ID + ":copper/mace_strike");

        // Full amethyst trim advancement
        AdvancementHolder resonatingVision = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.AMETHYST_SHARD,
                        Component.translatable("advancements.functional_trims.full_amethyst_trim.title"),
                        Component.translatable("advancements.functional_trims.full_amethyst_trim.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":full_amethyst_trim");

        // Amethyst trim sub-advancement
        Advancement.Builder.advancement()
                .parent(resonatingVision)
                .display(
                        Items.SPYGLASS,
                        Component.translatable("advancements.functional_trims.amethyst.wallhacks_enabled.title"),
                        Component.translatable("advancements.functional_trims.amethyst.wallhacks_enabled.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("wallhacks_enabled",
                        ModCriteria.TRIM_TRIGGER.criterion("amethyst", "wallhacks_enabled"))
                .save(consumer, FunctionalTrims.MOD_ID + ":amethyst/wallhacks_enabled");

        // Amethyst trim sub-advancement
        Advancement.Builder.advancement()
                .parent(resonatingVision)
                .display(
                        Items.ENDER_PEARL,
                        Component.translatable("advancements.functional_trims.amethyst.i_see_you.title"),
                        Component.translatable("advancements.functional_trims.amethyst.i_see_you.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true, true, true
                )
                .addCriterion("i_see_you",
                        ModCriteria.TRIM_TRIGGER.criterion("amethyst", "i_see_you"))
                .save(consumer, FunctionalTrims.MOD_ID + ":amethyst/i_see_you");

        // Full resin trim advancement
        AdvancementHolder adhesiveGrip = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.RESIN_BRICK,
                        Component.translatable("advancements.functional_trims.full_resin_trim.title"),
                        Component.translatable("advancements.functional_trims.full_resin_trim.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":full_resin_trim");

        // Resin trim sub-advancement
        Advancement.Builder.advancement()
                .parent(adhesiveGrip)
                .display(
                        Items.SLIME_BALL,
                        Component.translatable("advancements.functional_trims.resin.stick_to_wall.title"),
                        Component.translatable("advancements.functional_trims.resin.stick_to_wall.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("stick_to_wall",
                        ModCriteria.TRIM_TRIGGER.criterion("resin", "stick_to_wall"))
                .save(consumer, FunctionalTrims.MOD_ID + ":resin/stick_to_wall");

        // Resin trim sub-advancement
        Advancement.Builder.advancement()
                .parent(adhesiveGrip)
                .display(
                        Items.FEATHER,
                        Component.translatable("advancements.functional_trims.resin.long_fall.title"),
                        Component.translatable("advancements.functional_trims.resin.long_fall.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true, true, true
                )
                .addCriterion("long_fall",
                        ModCriteria.TRIM_TRIGGER.criterion("resin", "long_fall"))
                .save(consumer, FunctionalTrims.MOD_ID + ":resin/long_fall");

        // Full quartz trim advancement
        AdvancementHolder enrichedVitality = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.QUARTZ,
                        Component.translatable("advancements.functional_trims.full_quartz_trim.title"),
                        Component.translatable("advancements.functional_trims.full_quartz_trim.description"),
                        null,
                        AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("auto", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, FunctionalTrims.MOD_ID + ":full_quartz_trim");

        // Quartz trim sub-advancement
        Advancement.Builder.advancement()
                .parent(enrichedVitality)
                .display(
                        Items.GOLDEN_CARROT,
                        Component.translatable("advancements.functional_trims.quartz.eat_golden_carrot.title"),
                        Component.translatable("advancements.functional_trims.quartz.eat_golden_carrot.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("eat_golden_carrot",
                        ModCriteria.TRIM_TRIGGER.criterion("quartz", "eat_golden_carrot"))
                .save(consumer, FunctionalTrims.MOD_ID + ":quartz/eat_golden_carrot");

        // Quartz trim sub-advancement
        Advancement.Builder.advancement()
                .parent(enrichedVitality)
                .display(
                        Items.GLASS_BOTTLE,
                        Component.translatable("advancements.functional_trims.quartz.drink_potion.title"),
                        Component.translatable("advancements.functional_trims.quartz.drink_potion.description"),
                        null,
                        AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("drink_potion",
                        ModCriteria.TRIM_TRIGGER.criterion("quartz", "drink_potion"))
                .save(consumer, FunctionalTrims.MOD_ID + ":quartz/drink_potion");
    }
}