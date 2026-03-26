package functional_trims.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
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
 * Instead of creating a separate criterion for every trim effect,
 * we parameterize it with:
 * - material: which trim material caused the event (e.g. "redstone")
 * - triggerType: what happened (e.g. "activate_mechanism")
 * Then we call trigger(player, "redstone", "activate_mechanism") in gameplay code.
 */
public class TrimTriggerCriterion extends AbstractCriterion<TrimTriggerCriterion.Conditions> {
    public static final Identifier ID = Identifier.of(FunctionalTrims.MOD_ID, "trim_trigger");

    public Identifier getId() {
        return ID;
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, String material, String triggerType) {
        Predicate<Conditions> check = cond ->
                cond.material().equals(material) &&
                        cond.triggerType().equals(triggerType);
        super.trigger(player, check);
    }

    public AdvancementCriterion<Conditions> criterion(String material, String triggerType) {
        return this.create(new Conditions(
                /* player */ Optional.empty(),
                material,
                triggerType
        ));
    }

    public record Conditions(
            Optional<LootContextPredicate> player,
            String material,
            String triggerType
    ) implements AbstractCriterion.Conditions, CriterionConditions {
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
    }
}