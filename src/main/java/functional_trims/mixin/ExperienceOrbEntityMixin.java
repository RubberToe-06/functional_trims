package functional_trims.mixin;

import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Grants bonus experience for players wearing full Lapis-trimmed armor.
 * Runs only on the server side to avoid client-side ClassCastExceptions.
 */
@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {

    @Shadow
    private int amount; // shadowing private field

    @Inject(method = "onPlayerCollision", at = @At("HEAD"))
    private void functional_trims$boostExp(PlayerEntity player, CallbackInfo ci) {

        if (player.getEntityWorld().isClient()) return;
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
        if (!FTConfig.isTrimEnabled("lapis")) return;

        if (TrimHelper.countTrim(serverPlayer, ArmorTrimMaterials.LAPIS) < 4) return;

        float bonusMultiplier = 0.5f;
        int bonus = Math.max(1, (int)(this.amount * bonusMultiplier));
        serverPlayer.addExperience(bonus);

        // Trigger criteria for advancements
        ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "lapis", "absorb_xp_orb");
        if (serverPlayer.experienceLevel >= 100) {
            ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "lapis", "reach_level_100");
        }
    }
}
