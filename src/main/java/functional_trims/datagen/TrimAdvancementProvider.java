package functional_trims.datagen;

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
        Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.REDSTONE,
                        Text.literal("Fully Conductive"),
                        Text.literal("Your body hums with energy, powering the ground beneath your feet"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_redstone_trim");

        // Full emerald trim advancement
        Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.EMERALD,
                        Text.literal("Explorer's Fortune"),
                        Text.literal("Your emerald shine draws fortune from every chest you open"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_emerald_trim");

        // Full lapis trim advancement
        Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.LAPIS_LAZULI,
                        Text.literal("Scholar's Insight"),
                        Text.literal("Knowledge flows to you faster, wisdom clings to every spark of experience"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_lapis_trim");

        // Full gold trim advancement
        Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.GOLD_INGOT,
                        Text.literal("Lord of the Nether"),
                        Text.literal("Your golden armor commands respect from even the fiercest Piglins"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_gold_trim");

        // Full diamond trim advancement
        Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.DIAMOND,
                        Text.literal("Shattering Resilliance"),
                        Text.literal("Diamond's unwavering power shines through you until the very end"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_diamond_trim");

        // Full netherite trim advancement
        Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.NETHERITE_INGOT,
                        Text.literal("Immoveable Object"),
                        Text.literal("The weight of Netherite roots you to the earth, nothing moves you"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_netherite_trim");

        // Full iron trim advancement
        Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.IRON_INGOT,
                        Text.literal("Unyielding Defense"),
                        Text.literal("Arrows glance, crits falter, shields rise, iron endures"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_iron_trim");

        // Full copper trim advancement
        Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.COPPER_INGOT,
                        Text.literal("Supercharged Strike"),
                        Text.literal("Lightning strikes you once, and you strike back harder"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_copper_trim");

        // Full amethyst trim advancement
        Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.AMETHYST_SHARD,
                        Text.literal("Resonant Echo"),
                        Text.literal("Still your breath and sense the echoes of the world around you"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_amethyst_trim");

        // Full resin trim advancement
        Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.RESIN_BRICK,
                        Text.literal("Adhesive Grip"),
                        Text.literal("Where others slip and fall, the resin holds firm"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_resin_trim");

        // Full quartz trim advancement
        Advancement.Builder.create()
                .parent(root)
                .display(
                        Items.QUARTZ,
                        Text.literal("Enriched Vitality"),
                        Text.literal("Purity refines your essence, every blessing lasts a little longer"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                // 'auto' criterion matches what we’ll grant manually
                .criterion("auto", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
                .build(consumer, FunctionalTrims.MOD_ID + ":full_quartz_trim");


    }
}
