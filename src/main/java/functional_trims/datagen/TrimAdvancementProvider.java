package functional_trims.datagen;

import functional_trims.criteria.ModCriteria;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import functional_trims.FunctionalTrims;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TrimAdvancementProvider extends FabricAdvancementProvider {
    public TrimAdvancementProvider(FabricDataOutput output,
                                   CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }
    private static ItemStack createPotionIcon() {
        ItemStack potion = new ItemStack(Items.POTION);
        potion.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.STRENGTH));
        return potion;
    }
    @Override
    public void generateAdvancement(
        @NotNull RegistryWrapper.WrapperLookup registries,
        @NotNull Consumer<AdvancementEntry> consumer) {

        // Root
        AdvancementEntry root = Advancement.Builder.create()
                .display(
                        Items.SMITHING_TABLE,
                        Text.translatable("advancements.functional_trims.root.title"),
                        Text.translatable("advancements.functional_trims.root.description"),
                        Identifier.of("minecraft", "block/polished_blackstone_bricks"),
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":root");

        // Full redstone trim advancement
        AdvancementEntry powerWalk = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.REDSTONE,
                        Text.translatable("advancements.functional_trims.full_redstone_trim.title"),
                        Text.translatable("advancements.functional_trims.full_redstone_trim.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_redstone_trim");

        // Redstone trim sub-advancement
        Advancement.Builder.create()
                .parent(powerWalk)
                .display(
                        Items.REDSTONE_LAMP,
                        Text.translatable("advancements.functional_trims.redstone.redstone_lamp_activation.title"),
                        Text.translatable("advancements.functional_trims.redstone.redstone_lamp_activation.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("activate_lamp",
                        ModCriteria.TRIM_TRIGGER.criterion("redstone", "activate_lamp"))
                .build(consumer, FunctionalTrims.MOD_ID + ":redstone_lamp_activation");


        // Full emerald trim advancement
        AdvancementEntry explorersFortune = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.EMERALD,
                        Text.translatable("advancements.functional_trims.full_emerald_trim.title"),
                        Text.translatable("advancements.functional_trims.full_emerald_trim.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_emerald_trim");

        // Emerald trim sub-advancement
        Advancement.Builder.create()
                .parent(explorersFortune)
                .display(
                        Items.CHEST,
                        Text.translatable("advancements.functional_trims.emerald.1_in_7_5_trillion.title"),
                        Text.translatable("advancements.functional_trims.emerald.1_in_7_5_trillion.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("open_chest",
                        ModCriteria.TRIM_TRIGGER.criterion("emerald", "open_loot_chest"))
                .build(consumer, FunctionalTrims.MOD_ID + ":emerald/1_in_7.5_trillion");


        // Full lapis trim advancement
        AdvancementEntry scholarsInsight = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.LAPIS_LAZULI,
                        Text.translatable("advancements.functional_trims.full_lapis_trim.title"),
                        Text.translatable("advancements.functional_trims.full_lapis_trim.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_lapis_trim");

        // Lapis trim sub-advancement
        Advancement.Builder.create()
                .parent(scholarsInsight)
                .display(
                        Items.ENDER_EYE,
                        Text.translatable("advancements.functional_trims.lapis.level_100.title"),
                        Text.translatable("advancements.functional_trims.lapis.level_100.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, true
                )
                .criterion("reach_level_100",
                        ModCriteria.TRIM_TRIGGER.criterion("lapis", "reach_level_100"))
                .build(consumer, FunctionalTrims.MOD_ID + ":lapis/level_100");

        Advancement.Builder.create()
                .parent(scholarsInsight)
                .display(
                        Items.EXPERIENCE_BOTTLE,
                        Text.translatable("advancements.functional_trims.lapis.first_lesson.title"),
                        Text.translatable("advancements.functional_trims.lapis.first_lesson.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("absorb_xp_orb",
                        ModCriteria.TRIM_TRIGGER.criterion("lapis", "absorb_xp_orb"))
                .build(consumer, FunctionalTrims.MOD_ID + ":lapis/first_lesson");

        // Full gold trim advancement
        AdvancementEntry lordOfTheNether = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.GOLD_INGOT,
                        Text.translatable("advancements.functional_trims.full_gold_trim.title"),
                        Text.translatable("advancements.functional_trims.full_gold_trim.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_gold_trim");

        // Gold trim sub-advancement
        Advancement.Builder.create()
                .parent(lordOfTheNether)
                .display(
                        Items.GILDED_BLACKSTONE,
                        Text.translatable("advancements.functional_trims.gold.enter_bastion.title"),
                        Text.translatable("advancements.functional_trims.gold.enter_bastion.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("enter_bastion",
                        ModCriteria.TRIM_TRIGGER.criterion("gold", "enter_bastion"))
                .build(consumer, FunctionalTrims.MOD_ID + ":gold/enter_bastion");

        // Gold trim sub-advancement
        Advancement.Builder.create()
                .parent(lordOfTheNether)
                .display(
                        Items.GOLDEN_SWORD,
                        Text.translatable("advancements.functional_trims.gold.attack_piglin_brute.title"),
                        Text.translatable("advancements.functional_trims.gold.attack_piglin_brute.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("attack_piglin_brute",
                        ModCriteria.TRIM_TRIGGER.criterion("gold", "attack_piglin_brute"))
                .build(consumer, FunctionalTrims.MOD_ID + ":gold/attack_piglin_brute");

        // Full diamond trim advancement
        AdvancementEntry shatteringResilliance = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.DIAMOND,
                        Text.translatable("advancements.functional_trims.full_diamond_trim.title"),
                        Text.translatable("advancements.functional_trims.full_diamond_trim.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_diamond_trim");

        // Diamond trim sub-advancement
        Advancement.Builder.create()
                .parent(shatteringResilliance)
                .display(
                        Items.NETHER_STAR,
                        Text.translatable("advancements.functional_trims.diamond.armor_shatter.title"),
                        Text.translatable("advancements.functional_trims.diamond.armor_shatter.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("attack_piglin_brute",
                        ModCriteria.TRIM_TRIGGER.criterion("diamond", "armor_shatter"))
                .build(consumer, FunctionalTrims.MOD_ID + ":diamond/armor_shatter");

        // Full netherite trim advancement
        AdvancementEntry immoveableObject = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.NETHERITE_INGOT,
                        Text.translatable("advancements.functional_trims.full_netherite_trim.title"),
                        Text.translatable("advancements.functional_trims.full_netherite_trim.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_netherite_trim");

        // Netherite trim sub-advancement
        Advancement.Builder.create()
                .parent(immoveableObject)
                .display(
                        Items.TNT,
                        Text.translatable("advancements.functional_trims.netherite.that_was_cute.title"),
                        Text.translatable("advancements.functional_trims.netherite.that_was_cute.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("resist_explosion",
                        ModCriteria.TRIM_TRIGGER.criterion("netherite", "resist_explosion"))
                .build(consumer, FunctionalTrims.MOD_ID + ":netherite/that_was_cute");

        // Full iron trim advancement
        AdvancementEntry unyieldingDefense = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.IRON_INGOT,
                        Text.translatable("advancements.functional_trims.full_iron_trim.title"),
                        Text.translatable("advancements.functional_trims.full_iron_trim.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_iron_trim");

        // Iron trim sub-advancement
        Advancement.Builder.create()
                .parent(unyieldingDefense)
                .display(
                        Items.ARROW,
                        Text.translatable("advancements.functional_trims.iron.reflect_projectile.title"),
                        Text.translatable("advancements.functional_trims.iron.reflect_projectile.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("reflect_projectile",
                        ModCriteria.TRIM_TRIGGER.criterion("iron", "reflect_projectile"))
                .build(consumer, FunctionalTrims.MOD_ID + ":iron/reflect_projectile");

        // Iron trim sub-advancement
        Advancement.Builder.create()
                .parent(unyieldingDefense)
                .display(
                        Items.SHIELD,
                        Text.translatable("advancements.functional_trims.iron.knockback_attacker.title"),
                        Text.translatable("advancements.functional_trims.iron.knockback_attacker.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("knockback_attacker",
                        ModCriteria.TRIM_TRIGGER.criterion("iron", "knockback_attacker"))
                .build(consumer, FunctionalTrims.MOD_ID + ":iron/knockback_attacker");

        // Full copper trim advancement
        AdvancementEntry superchargedStrike = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.COPPER_INGOT,
                        Text.translatable("advancements.functional_trims.full_copper_trim.title"),
                        Text.translatable("advancements.functional_trims.full_copper_trim.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_copper_trim");

        // Copper trim sub-advancement
        Advancement.Builder.create()
                .parent(superchargedStrike)
                .display(
                        Items.LIGHTNING_ROD,
                        Text.translatable("advancements.functional_trims.copper.struck_by_lightning.title"),
                        Text.translatable("advancements.functional_trims.copper.struck_by_lightning.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("struck_by_lightning",
                        ModCriteria.TRIM_TRIGGER.criterion("copper", "struck_by_lightning"))
                .build(consumer, FunctionalTrims.MOD_ID + ":copper/struck_by_lightning");

        // Copper trim sub-advancement
        Advancement.Builder.create()
                .parent(superchargedStrike)
                .display(
                        Items.MACE,
                        Text.translatable("advancements.functional_trims.copper.mace_strike.title"),
                        Text.translatable("advancements.functional_trims.copper.mace_strike.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("mace_strike",
                        ModCriteria.TRIM_TRIGGER.criterion("copper", "mace_strike"))
                .build(consumer, FunctionalTrims.MOD_ID + ":copper/mace_strike");

        // Full amethyst trim advancement
        AdvancementEntry resonatingVision = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.AMETHYST_SHARD,
                        Text.translatable("advancements.functional_trims.full_amethyst_trim.title"),
                        Text.translatable("advancements.functional_trims.full_amethyst_trim.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_amethyst_trim");

        // Amethyst trim sub-advancement
        Advancement.Builder.create()
                .parent(resonatingVision)
                .display(
                        Items.SPYGLASS,
                        Text.translatable("advancements.functional_trims.amethyst.wallhacks_enabled.title"),
                        Text.translatable("advancements.functional_trims.amethyst.wallhacks_enabled.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("wallhacks_enabled",
                        ModCriteria.TRIM_TRIGGER.criterion("amethyst", "wallhacks_enabled"))
                .build(consumer, FunctionalTrims.MOD_ID + ":amethyst/wallhacks_enabled");

        // Amethyst trim sub-advancement
        Advancement.Builder.create()
                .parent(resonatingVision)
                .display(
                        Items.ENDER_PEARL,
                        Text.translatable("advancements.functional_trims.amethyst.i_see_you.title"),
                        Text.translatable("advancements.functional_trims.amethyst.i_see_you.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, true
                )
                .criterion("i_see_you",
                        ModCriteria.TRIM_TRIGGER.criterion("amethyst", "i_see_you"))
                .build(consumer, FunctionalTrims.MOD_ID + ":amethyst/i_see_you");

        // Full resin trim advancement
        AdvancementEntry adhesiveGrip = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.RESIN_BRICK,
                        Text.translatable("advancements.functional_trims.full_resin_trim.title"),
                        Text.translatable("advancements.functional_trims.full_resin_trim.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_resin_trim");

        // Resin trim sub-advancement
        Advancement.Builder.create()
                .parent(adhesiveGrip)
                .display(
                        Items.SLIME_BALL,
                        Text.translatable("advancements.functional_trims.resin.stick_to_wall.title"),
                        Text.translatable("advancements.functional_trims.resin.stick_to_wall.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("stick_to_wall",
                        ModCriteria.TRIM_TRIGGER.criterion("resin", "stick_to_wall"))
                .build(consumer, FunctionalTrims.MOD_ID + ":resin/stick_to_wall");

        // Resin trim sub-advancement
        Advancement.Builder.create()
                .parent(adhesiveGrip)
                .display(
                        Items.FEATHER,
                        Text.translatable("advancements.functional_trims.resin.long_fall.title"),
                        Text.translatable("advancements.functional_trims.resin.long_fall.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, true
                )
                .criterion("long_fall",
                        ModCriteria.TRIM_TRIGGER.criterion("resin", "long_fall"))
                .build(consumer, FunctionalTrims.MOD_ID + ":resin/long_fall");

        // Full quartz trim advancement
        AdvancementEntry enrichedVitality = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.QUARTZ,
                        Text.translatable("advancements.functional_trims.full_quartz_trim.title"),
                        Text.translatable("advancements.functional_trims.full_quartz_trim.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_quartz_trim");

        // Quartz trim sub-advancement
        Advancement.Builder.create()
                .parent(enrichedVitality)
                .display(
                        Items.GOLDEN_CARROT,
                        Text.translatable("advancements.functional_trims.quartz.eat_golden_carrot.title"),
                        Text.translatable("advancements.functional_trims.quartz.eat_golden_carrot.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("eat_golden_carrot",
                        ModCriteria.TRIM_TRIGGER.criterion("quartz", "eat_golden_carrot"))
                .build(consumer, FunctionalTrims.MOD_ID + ":quartz/eat_golden_carrot");

        // Quartz trim sub-advancement
        Advancement.Builder.create()
                .parent(enrichedVitality)
                .display(
                        createPotionIcon(),
                        Text.translatable("advancements.functional_trims.quartz.drink_potion.title"),
                        Text.translatable("advancements.functional_trims.quartz.drink_potion.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("drink_potion",
                        ModCriteria.TRIM_TRIGGER.criterion("quartz", "drink_potion"))
                .build(consumer, FunctionalTrims.MOD_ID + ":quartz/drink_potion");
    }
}