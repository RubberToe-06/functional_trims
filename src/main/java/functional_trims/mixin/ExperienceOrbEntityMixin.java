package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbEntityMixin {
    @Inject(method = "playerTouch", at = @At("HEAD"))
    private void functional_trims$boostExp(Player player, CallbackInfo ci) {
        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!FTConfig.isTrimEnabled("lapis")) return;

        int trimCount = TrimHelper.countTrim(serverPlayer, TrimMaterials.LAPIS);
        if (trimCount < 4) return;

        ExperienceOrb orb = (ExperienceOrb) (Object) this;

        float bonusMultiplier = ConfigManager.get().lapis.extraExpMultiplier;
        int bonus = Math.max(1, (int) (orb.getValue() * bonusMultiplier));
        serverPlayer.giveExperiencePoints(bonus);

        ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "lapis", "absorb_xp_orb");
        if (serverPlayer.experienceLevel >= 100) {
            ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "lapis", "reach_level_100");
        }
    }
}