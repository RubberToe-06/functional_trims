package functional_trims.trim_effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ChargedEffect extends StatusEffect {

    private static final UUID SPEED_MODIFIER_ID = UUID.fromString("1b853c68-0d23-4b41-a1a4-8b3cb1f2028e");
    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("dc9822e4-4b86-4db5-a6cf-f8e58a20c9ac");

    public ChargedEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xE88032);

        this.addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                Identifier.of(SPEED_MODIFIER_ID.toString()),
                0.20,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        this.addAttributeModifier(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                Identifier.of(ATTACK_SPEED_MODIFIER_ID.toString()),
                0.25,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}
