package functional_trims.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import functional_trims.FunctionalTrims;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

/**
 * A generic advancement trigger for Functional Trims.
 * Instead of creating a separate criterion for every trim effect,
 * we parameterize it with:
 * - material: which trim material caused the event (e.g. "redstone")
 * - triggerType: what happened (e.g. "activate_mechanism")
 * Then we call trigger(player, "redstone", "activate_mechanism") in gameplay code.
 */
public class TrimTriggerCriterion extends SimpleCriterionTrigger<TrimTriggerCriterion.Conditions> {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(FunctionalTrims.MOD_ID, "trim_trigger");

    public Identifier getId() {
        return ID;
    }

    @Override
    public @NonNull Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, String material, String triggerType) {
        Predicate<Conditions> check = cond ->
                cond.material().equals(material) &&
                        cond.triggerType().equals(triggerType);
        super.trigger(player, check);
    }

    public Criterion<Conditions> criterion(String material, String triggerType) {
        return this.createCriterion(new Conditions(
                /* player */ Optional.empty(),
                material,
                triggerType
        ));
    }

    public record Conditions(
            Optional<ContextAwarePredicate> player,
            String material,
            String triggerType
    ) implements SimpleCriterionTrigger.SimpleInstance, CriterionTriggerInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ContextAwarePredicate.CODEC
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