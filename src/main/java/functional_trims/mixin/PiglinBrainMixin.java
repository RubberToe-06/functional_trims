package functional_trims.mixin;

import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.trim.ArmorTrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Comprehensive piglin pacification:
 * - Any player wearing gold-trimmed armor is treated as friendly.
 * - Applies to all piglins.
 * - Also clears anger/pathfinding so they don't run at the player.
 * - Extends vanilla gold armor safety logic.
 */
@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {

    /**
     * Prevent piglins from selecting gold-trimmed players as targets,
     * and immediately clear any anger/pathing toward them.
     */
    @Inject(
            method = "getPreferredTarget(Lnet/minecraft/entity/mob/PiglinEntity;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void functional_trims$pacifyGoldTrimmedPlayers(PiglinEntity piglin,
                                                                  CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        piglin.getBrain()
                .getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)
                .filter(target -> target instanceof PlayerEntity player &&
                        TrimHelper.countTrim(player, ArmorTrimMaterials.GOLD) > 0)
                .ifPresent(target -> {
                    piglin.getBrain().forget(MemoryModuleType.ANGRY_AT);
                    piglin.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
                    piglin.setAttacking(false);

                    cir.setReturnValue(Optional.empty());
                });
    }


    /**
     * Prevent anger when gold-trimmed players open chests or mine gold-related blocks.
     */
    @Inject(
            method = "onGuardedBlockInteracted(Lnet/minecraft/entity/player/PlayerEntity;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void functional_trims$preventAngerOnGoldTrim(PlayerEntity player, boolean blockOpen, CallbackInfo ci) {
        if (!FTConfig.isTrimEnabled("gold")) return;
        if (TrimHelper.countTrim(player, ArmorTrimMaterials.GOLD) == 4) {
            ci.cancel();
        }
    }

    /**
     * Extend vanilla logic: treat full gold-trimmed armor as “piglin-safe”.
     */
    @Inject(
            method = "wearsGoldArmor(Lnet/minecraft/entity/LivingEntity;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void functional_trims$goldTrimCountsAsSafe(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!FTConfig.isTrimEnabled("gold")) return;

        if (entity instanceof PlayerEntity player) {
            int goldTrims = TrimHelper.countTrim(player, ArmorTrimMaterials.GOLD);
            if (goldTrims == 4) { // full set only
                cir.setReturnValue(true);
            }
        }
    }
}
