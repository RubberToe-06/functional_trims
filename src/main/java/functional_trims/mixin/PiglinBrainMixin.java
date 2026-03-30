package functional_trims.mixin;

import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

/**
 * Comprehensive piglin pacification:
 * - Any player wearing gold-trimmed armor is treated as friendly.
 * - Applies to all piglins.
 * - Also clears anger/pathfinding so they don't run at the player.
 * - Extends vanilla gold armor safety logic.
 */
@Mixin(PiglinAi.class)
public class PiglinBrainMixin {

    /**
     * Prevent piglins from selecting gold-trimmed players as targets,
     * and immediately clear any anger/pathing toward them.
     */
    @Inject(
            method = "findNearestValidAttackTarget(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/monster/piglin/Piglin;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void functional_trims$pacifyGoldTrimmedPlayers(ServerLevel serverLevel, Piglin piglin,
                                                                  CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        piglin.getBrain()
                .getMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                .filter(target -> target instanceof Player player &&
                        TrimHelper.countTrim(player, TrimMaterials.GOLD) > 0)
                .ifPresent(target -> {
                    // Clear anger memories, but don't freeze their AI
                    piglin.getBrain().eraseMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ANGRY_AT);
                    piglin.getBrain().eraseMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET);
                    piglin.setAggressive(false);

                    cir.setReturnValue(Optional.empty());
                });
    }


    /**
     * Prevent anger when gold-trimmed players open chests or mine gold-related blocks.
     */
    @Inject(
            method = "angerNearbyPiglins(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void functional_trims$preventAngerOnGoldTrim(ServerLevel serverLevel, Player player, boolean bl, CallbackInfo ci) {
        if (!FTConfig.isTrimEnabled("gold")) return;
        if (TrimHelper.countTrim(player, TrimMaterials.GOLD) == 4) {
            ci.cancel();
        }
    }

    /**
     * Extend vanilla logic: treat full gold-trimmed armor as “piglin-safe”.
     */
    @Inject(
            method = "isWearingSafeArmor(Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void functional_trims$goldTrimCountsAsSafe(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!FTConfig.isTrimEnabled("gold")) return;

        if (livingEntity instanceof Player player) {
            int goldTrims = TrimHelper.countTrim(player, TrimMaterials.GOLD);
            if (goldTrims == 4) { // full set only
                cir.setReturnValue(true);
            }
        }
    }
}