package functional_trims.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.function.Predicate;

public class TrimTriggerCriterion
        extends SimpleCriterionTrigger<TrimTriggerCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(
            ServerPlayer player,
            String material,
            String triggerType) {

        Predicate<Conditions> check = conditions ->
                conditions.material().equals(material)
                        && conditions.triggerType().equals(triggerType);

        super.trigger(player, check);
    }

    public Criterion<Conditions> criterion(
            String material,
            String triggerType) {

        return this.createCriterion(
                new Conditions(
                        Optional.empty(),
                        material,
                        triggerType
                )
        );
    }

    public record Conditions(
            Optional<ContextAwarePredicate> player,
            String material,
            String triggerType)
            implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Conditions> CODEC =
                RecordCodecBuilder.create(instance ->
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