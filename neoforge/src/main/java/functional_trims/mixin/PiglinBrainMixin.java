package functional_trims.mixin;

import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinAi.class)
public class PiglinBrainMixin {

    @Inject(
            method = "findNearestValidAttackTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void functional_trims$pacifyGoldTrimmedPlayers(
            ServerLevel level,
            Piglin piglin,
            CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {

        piglin.getBrain()
                .getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                .filter(player ->
                        TrimHelper.countTrim(
                                player,
                                TrimMaterials.GOLD
                        ) > 0)
                .ifPresent(player -> {
                    piglin.getBrain()
                            .eraseMemory(
                                    MemoryModuleType.ANGRY_AT
                            );

                    piglin.getBrain()
                            .eraseMemory(
                                    MemoryModuleType.ATTACK_TARGET
                            );

                    piglin.setAggressive(false);

                    cir.setReturnValue(
                            Optional.empty()
                    );
                });
    }

    @Inject(
            method = "angerNearbyPiglins",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void functional_trims$preventAngerOnGoldTrim(
            ServerLevel level,
            Player player,
            boolean requireLineOfSight,
            CallbackInfo ci) {

        if (!FTConfig.isTrimEnabled("gold")) {
            return;
        }

        if (TrimHelper.countTrim(
                player,
                TrimMaterials.GOLD
        ) == 4) {
            ci.cancel();
        }
    }

    @Inject(
            method = "isWearingSafeArmor",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void functional_trims$goldTrimCountsAsSafe(
            LivingEntity entity,
            CallbackInfoReturnable<Boolean> cir) {

        if (cir.getReturnValue()) {
            return;
        }

        if (!FTConfig.isTrimEnabled("gold")) {
            return;
        }

        if (entity instanceof Player player) {
            int goldTrims =
                    TrimHelper.countTrim(
                            player,
                            TrimMaterials.GOLD
                    );

            if (goldTrims == 4) {
                cir.setReturnValue(true);
            }
        }
    }
}