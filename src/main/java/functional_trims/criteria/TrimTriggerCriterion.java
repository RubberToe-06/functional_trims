package functional_trims.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import functional_trims.FunctionalTrims;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * A generic advancement trigger for Functional Trims.
 *
 * Instead of creating a separate criterion for every trim effect,
 * we parameterize it with:
 *
 * - material: which trim material caused the event (e.g. "redstone")
 * - triggerType: what happened (e.g. "activate_mechanism")
 *
 * Then we call trigger(player, "redstone", "activate_mechanism") in gameplay code.
 */
public class TrimTriggerCriterion extends AbstractCriterion<TrimTriggerCriterion.Conditions> {
    public static final Identifier ID = Identifier.of(FunctionalTrims.MOD_ID, "trim_trigger");

    public Identifier getId() {
        return ID;
    }

    // 1.21+ criteria MUST expose a Codec for their Conditions.
    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    /**
     * Call this from your gameplay logic when the effect actually fires.
     * Example: when redstone trim powers a block.
     */
    public void trigger(ServerPlayerEntity player, String material, String triggerType) {
        // AbstractCriterion#trigger(ServerPlayerEntity, Predicate<T>)
        // loops all listeners for that player and grants if the predicate returns true. :contentReference[oaicite:14]{index=14}
        Predicate<Conditions> check = cond ->
                cond.material().equals(material) &&
                        cond.triggerType().equals(triggerType);

        this.trigger(player, check);
    }

    /**
     * Helper for datagen: build an AdvancementCriterion for
     * (material=X, triggerType=Y) without writing raw JSON.
     *
     * Usage in your provider:
     * .criterion("activate_mechanism",
     *     ModCriteria.TRIM_TRIGGER.criterion("redstone", "activate_mechanism"))
     */
    public net.minecraft.advancement.AdvancementCriterion<Conditions> criterion(String material, String triggerType) {
        return this.create(new Conditions(
                /* player */ Optional.empty(),
                material,
                triggerType
        ));
    }

    /**
     * Conditions record. Matches Mojang's pattern (see e.g. LightningStrikeCriterion.Conditions). :contentReference[oaicite:15]{index=15}
     *
     * - player: Optional<LootContextPredicate> that can restrict "which player"
     *   (we're not using it yet, so it'll usually be Optional.empty()).
     * - material: which trim material must have caused it.
     * - triggerType: which event type we're talking about.
     */
    public static record Conditions(
            Optional<LootContextPredicate> player,
            String material,
            String triggerType
    ) implements AbstractCriterion.Conditions, CriterionConditions {

        // The CODEC defines how this condition is read from advancement JSON.
        // Matches Mojang style:
        //   optionalFieldOf("player") for Optional<LootContextPredicate>
        //   fieldOf(...) for required strings, etc. :contentReference[oaicite:16]{index=16} :contentReference[oaicite:17]{index=17}
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        LootContextPredicate.CODEC
                                .optionalFieldOf("player")
                                .forGetter(Conditions::player),
                        Codec.STRING
                                .fieldOf("material")
                                .forGetter(Conditions::material),
                        Codec.STRING
                                .fieldOf("trigger_type")
                                .forGetter(Conditions::triggerType)
                ).apply(instance, Conditions::new)
        );

        // AbstractCriterion.Conditions already provides a default validate(...)
        // implementation (the vanilla records don't all override it). :contentReference[oaicite:18]{index=18} :contentReference[oaicite:19]{index=19}
    }
}
