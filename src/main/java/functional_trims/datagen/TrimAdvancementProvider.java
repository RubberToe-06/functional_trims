package functional_trims.datagen;

import functional_trims.criteria.ModCriteria;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import functional_trims.FunctionalTrims;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TrimAdvancementProvider extends FabricAdvancementProvider {
    public TrimAdvancementProvider(FabricDataOutput output,
                                   CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registries,
                                    Consumer<AdvancementEntry> consumer) {
        // Root (optional)
        AdvancementEntry root = Advancement.Builder.create()
                .display(
                        Items.SMITHING_TABLE,
                        Text.literal("Functional Trims"),
                        Text.literal("Harness the power of armor trims!"),
                        Identifier.of(FunctionalTrims.MOD_ID, "gui/advancements/backgrounds/functional_trims"),
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
                        Text.literal("Power Walk"),
                        Text.literal("Equip a full set of redstone trimmed armor"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_redstone_trim");

        // Redstone trim sub-advancement
        Advancement.Builder.create()
                .parent(powerWalk)
                .display(
                        Items.REDSTONE_LAMP,
                        Text.literal("Walking on Sunshine"),
                        Text.literal("Power a redstone lamp by walking on it with redstone-trimmed armor"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // Real custom criterion using TrimTriggerCriterion
                .criterion("activate_lamp",
                        ModCriteria.TRIM_TRIGGER.criterion("redstone", "activate_lamp"))
                .build(consumer, FunctionalTrims.MOD_ID + ":redstone_lamp_activation");


        // Full emerald trim advancement
        AdvancementEntry explorersFortune = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.EMERALD,
                        Text.literal("Explorer's Fortune"),
                        Text.literal("Equip a full set of emerald trimmed armor"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_emerald_trim");

        // Emerald trim sub-advancement
        Advancement.Builder.create()
                .parent(explorersFortune)
                .display(
                        Items.CHEST,
                        Text.literal("1 in 7.5 trillion"),
                        Text.literal("Open a loot chest while wearing emerald-trimmed armor."),
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
                        Text.literal("Scholar's Insight"),
                        Text.literal("Equip a full set of lapis trimmed armor"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_lapis_trim");

        // Lapis trim sub-advancement 1
        Advancement.Builder.create()
                .parent(scholarsInsight)
                .display(
                        Items.ENDER_EYE,
                        Text.literal("Infinite Wisdom"),
                        Text.literal("Reach level 100 while wearing lapis trimmed armor."),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("reach_level_100",
                        ModCriteria.TRIM_TRIGGER.criterion("lapis", "reach_level_100"))
                .build(consumer, FunctionalTrims.MOD_ID + ":lapis/level_100");

        Advancement.Builder.create()
                .parent(scholarsInsight)
                .display(
                        Items.EXPERIENCE_BOTTLE,
                        Text.literal("Extra Credit"),
                        Text.literal("Absorb an experience orb while wearing lapis trimmed armor."),
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
                        Text.literal("Lord of the Nether"),
                        Text.literal("Equip a full set of gold trimmed armor"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_gold_trim");

        // Gold trim sub-advancement 1
        Advancement.Builder.create()
                .parent(lordOfTheNether)
                .display(
                        Items.GILDED_BLACKSTONE,
                        Text.literal("Royal Visit"),
                        Text.literal("Enter a Bastion Remnant while wearing gold-trimmed armor."),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("enter_bastion",
                        ModCriteria.TRIM_TRIGGER.criterion("gold", "enter_bastion"))
                .build(consumer, FunctionalTrims.MOD_ID + ":gold/enter_bastion");

        // Gold trim sub-advancement 2
        Advancement.Builder.create()
                .parent(lordOfTheNether)
                .display(
                        Items.GOLDEN_SWORD,
                        Text.literal("Establishing Dominance"),
                        Text.literal("Attack a Piglin Brute while wearing gold-trimmed armor."),
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
                        Text.literal("Shattering Resilliance"),
                        Text.literal("Equip a full set of diamond trimmed armor"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_diamond_trim");

        // Diamond sub-advancement
        Advancement.Builder.create()
                .parent(shatteringResilliance)
                .display(
                        Items.NETHER_STAR,
                        Text.literal("Noble Sacrifice"),
                        Text.literal("Take fatal damage while wearing diamond trimmed armor."),
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
                        Text.literal("Immoveable Object"),
                        Text.literal("Equip a full set of netherite trimmed armor"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_netherite_trim");

        // Full iron trim advancement
        AdvancementEntry unyieldingDefense = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.IRON_INGOT,
                        Text.literal("Unyielding Defense"),
                        Text.literal("Equip a full set of iron trimmed armor"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_iron_trim");

        // Full copper trim advancement
        AdvancementEntry superchargedStrike = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.COPPER_INGOT,
                        Text.literal("Supercharged Strike"),
                        Text.literal("Equip a full set of copper trimmed armor"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_copper_trim");

        // Full amethyst trim advancement
        AdvancementEntry resonatingVision = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.AMETHYST_SHARD,
                        Text.literal("Resonating Vision"),
                        Text.literal("Equip a full set of amethyst trimmed armor"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_amethyst_trim");

        // Full resin trim advancement
        AdvancementEntry adhesiveGrip = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.RESIN_BRICK,
                        Text.literal("Adhesive Grip"),
                        Text.literal("Equip a full set of resin trimmed armor"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_resin_trim");

        // Full quartz trim advancement
        AdvancementEntry enrichedVitality = Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.QUARTZ,
                        Text.literal("Enriched Vitality"),
                        Text.literal("Equip a full set of quartz trimmed armor"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_quartz_trim");


    }
}
