package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PiglinBruteAi.class)
public class PiglinBruteBrainMixin {

    @Inject(
            method = "findNearestValidAttackTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void functional_trims$pacifyGoldTrimmedPlayers(
            ServerLevel level,
            AbstractPiglin piglinBrute,
            CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {

        boolean piglinsDistractible =
                ConfigManager.get()
                        .gold
                        .distractPiglinBrutesEnabled;

        if (!FTConfig.isTrimEnabled("gold")) {
            return;
        }

        if (!piglinsDistractible) {
            return;
        }

        piglinBrute.getBrain()
                .getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                .filter(player ->
                        TrimHelper.countTrim(
                                player,
                                TrimMaterials.GOLD
                        ) > 0)
                .ifPresent(player -> {
                    piglinBrute.getBrain()
                            .eraseMemory(
                                    MemoryModuleType.ANGRY_AT
                            );

                    piglinBrute.getBrain()
                            .eraseMemory(
                                    MemoryModuleType.ATTACK_TARGET
                            );

                    piglinBrute.setAggressive(false);

                    cir.setReturnValue(
                            Optional.empty()
                    );
                });
    }
}