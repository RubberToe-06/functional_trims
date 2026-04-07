package functional_trims.mixin;

import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles explosion knockback for Netherite-trimmed players safely.
 * NOTE:
 * - Explosion knockback does NOT use LivingEntity#takeKnockback.
 * - The explosion pipeline checks Entity#isImmuneToExplosion(Explosion).
 * - Returning true here prevents explosion knockback (and also explosion damage).
 */
@Mixin(Entity.class)
public abstract class NetheriteImmovableMixin {

    // Tracks the last game tick feedback (sound + criteria) was sent per player,
    // so a single explosion event (which calls ignoreExplosion many times) only fires once.
    @Unique private static final Map<UUID, Long> lastResistTick = new HashMap<>();

    @Inject(method = "ignoreExplosion", at = @At("HEAD"), cancellable = true)
    private void functional_trims$netheriteTrimExplosionImmunity(Explosion explosion, CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;

        if (!(self instanceof ServerPlayer player)) return;
        if (TrimHelper.countTrim(player, TrimMaterials.NETHERITE) < 4) return;
        if (!FTConfig.isTrimEnabled("netherite")) return;

        // Fire feedback only once per game tick per player
        long currentTick = player.level().getGameTime();
        if (lastResistTick.getOrDefault(player.getUUID(), -1L) != currentTick) {
            lastResistTick.put(player.getUUID(), currentTick);

            ModCriteria.TRIM_TRIGGER.trigger(player, "netherite", "resist_explosion");

            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.NETHERITE_BLOCK_STEP,
                    SoundSource.PLAYERS,
                    0.7f,
                    0.5f
            );
        }

        // Immunity to explosion knockback (and damage) — always
        cir.setReturnValue(true);
    }
}