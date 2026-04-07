package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    /**
     * Scale duration for beneficial effects as they're applied.
     * Method exists in 1.21.8: addStatusEffect(StatusEffectInstance, Entity)
     */
    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"),
            argsOnly = true,
            name = "newEffect")
    private MobEffectInstance functionalTrims$quartz_extendPositive(MobEffectInstance newEffect) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (!(self instanceof ServerPlayer player)) return newEffect;
        if (!(TrimHelper.countTrim(  player, TrimMaterials.QUARTZ) == 4)) return newEffect;
        if (!FTConfig.isTrimEnabled("quartz")) return newEffect;

        // Only buff beneficial effects
        if (newEffect.getEffect().value().getCategory() != MobEffectCategory.BENEFICIAL) return newEffect;

        float potionMult = ConfigManager.get().quartz.potionEffectDurationMultiplier;
        int boostedDur = Math.round(newEffect.getDuration() * potionMult);
        ModCriteria.TRIM_TRIGGER.trigger(player, "quartz", "drink_potion");
        return new MobEffectInstance(
                newEffect.getEffect(),
                boostedDur,
                newEffect.getAmplifier(),
                newEffect.isAmbient(),
                newEffect.isVisible(),
                newEffect.showIcon()
        );

    }
}