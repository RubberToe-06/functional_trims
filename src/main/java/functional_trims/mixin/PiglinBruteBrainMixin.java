package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

/**
 * Makes Piglin Brutes fully docile toward players wearing gold-trimmed armor.
 * Clears all hostility memories and stops movement immediately.
 */
@Mixin(PiglinBruteAi.class)
public class PiglinBruteBrainMixin {

    @Inject(
            method = "findNearestValidAttackTarget(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/monster/piglin/AbstractPiglin;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void functional_trims$pacifyGoldTrimmedPlayers(ServerLevel serverLevel, AbstractPiglin abstractPiglin,
                                                                  CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        boolean PIGLINS_DISTRACTIBLE = ConfigManager.get().gold.distractPiglinBrutesEnabled;
        if (!FTConfig.isTrimEnabled("gold")) return;
        if (!PIGLINS_DISTRACTIBLE) return;
        abstractPiglin.getBrain()
                .getMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER)
                .filter(target -> target instanceof Player player &&
                        TrimHelper.countTrim(player, TrimMaterials.GOLD) > 0)
                .ifPresent(target -> {
                    // Clear anger memories, but let them keep moving/idle
                    abstractPiglin.getBrain().eraseMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ANGRY_AT);
                    abstractPiglin.getBrain().eraseMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET);
                    abstractPiglin.setAggressive(false);

                    cir.setReturnValue(Optional.empty());
                });
    }
}