package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import functional_trims.trim_effect.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    private static final float POTION_MULT = ConfigManager.get().potionEffectDurationMultiplier;

    /**
     * Scale duration for beneficial effects as they're applied.
     * Method exists in 1.21.8: addStatusEffect(StatusEffectInstance, Entity)
     */
    @ModifyVariable(
            method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private StatusEffectInstance functionalTrims$quartz_extendPositive(StatusEffectInstance original) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (!(self instanceof ServerPlayerEntity player)) return original;
        if (!(TrimHelper.countTrim(  player, ArmorTrimMaterials.QUARTZ) == 4)) return original;
        if (!FTConfig.isTrimEnabled("quartz")) return original;

        // Only buff beneficial effects
        if (original.getEffectType().value().getCategory() != StatusEffectCategory.BENEFICIAL) return original;

        int boostedDur = Math.round(original.getDuration() * POTION_MULT);
        ModCriteria.TRIM_TRIGGER.trigger(player, "quartz", "drink_potion");
        return new StatusEffectInstance(
                original.getEffectType(),
                boostedDur,
                original.getAmplifier(),
                original.isAmbient(),
                original.shouldShowParticles(),
                original.shouldShowIcon()
        );

    }
}
