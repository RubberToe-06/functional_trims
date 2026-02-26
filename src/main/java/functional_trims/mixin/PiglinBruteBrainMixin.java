package functional_trims.mixin;

import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinBruteBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.trim.ArmorTrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;

/**
 * Makes Piglin Brutes fully docile toward players wearing gold-trimmed armor.
 * Clears all hostility memories and stops movement immediately.
 */
@Mixin(PiglinBruteBrain.class)
public class PiglinBruteBrainMixin {

    @Inject(
            method = "getTarget(Lnet/minecraft/entity/mob/AbstractPiglinEntity;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void functional_trims$pacifyGoldTrimmedPlayers(AbstractPiglinEntity piglin,
                                                                  CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        if (!FTConfig.isTrimEnabled("gold")) return;

        piglin.getBrain()
                .getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)
                .filter(target -> target instanceof PlayerEntity player &&
                        TrimHelper.countTrim(player, ArmorTrimMaterials.GOLD) == 4)
                .ifPresent(target -> {
                    piglin.getBrain().forget(MemoryModuleType.ANGRY_AT);
                    piglin.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
                    piglin.setAttacking(false);

                    cir.setReturnValue(Optional.empty());
                });
    }

}
