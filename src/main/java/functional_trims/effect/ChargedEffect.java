package functional_trims.effect;

import java.util.UUID;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ChargedEffect extends MobEffect {

    private static final UUID SPEED_MODIFIER_ID = UUID.fromString("1b853c68-0d23-4b41-a1a4-8b3cb1f2028e");
    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("dc9822e4-4b86-4db5-a6cf-f8e58a20c9ac");
    private static final UUID BLOCK_BREAK_SPEED_MODIFIER_ID = UUID.fromString("5d5d69b7-c301-4d1b-b0b6-0f6fd4c81a12");

    public ChargedEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xE88032);

        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                Identifier.parse(SPEED_MODIFIER_ID.toString()),
                0.20,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        this.addAttributeModifier(
                Attributes.ATTACK_SPEED,
                Identifier.parse(ATTACK_SPEED_MODIFIER_ID.toString()),
                0.25,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        this.addAttributeModifier(
                Attributes.BLOCK_BREAK_SPEED,
                Identifier.parse(BLOCK_BREAK_SPEED_MODIFIER_ID.toString()),
                0.20,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}