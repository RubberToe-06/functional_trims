package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin(
            EntityType<?> entityType,
            Level level) {

        super(entityType, level);
    }

    @Unique
    private static final float POTION_MULT =
            ConfigManager.get()
                    .quartz
                    .potionEffectDurationMultiplier;

    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private MobEffectInstance functionalTrims$quartzExtendPositive(
            MobEffectInstance original) {

        ServerPlayer player =
                functionalTrims$asServerPlayer(this);

        if (player == null) {
            return original;
        }

        if (TrimHelper.countTrim(
                player,
                TrimMaterials.QUARTZ
        ) != 4) {
            return original;
        }

        if (!FTConfig.isTrimEnabled("quartz")) {
            return original;
        }

        if (original.getEffect()
                .value()
                .getCategory()
                != MobEffectCategory.BENEFICIAL) {

            return original;
        }

        int boostedDuration =
                Math.round(
                        original.getDuration()
                                * POTION_MULT
                );

        ModCriteria.TRIM_TRIGGER
                .get()
                .trigger(
                        player,
                        "quartz",
                        "drink_potion"
                );

        return new MobEffectInstance(
                original.getEffect(),
                boostedDuration,
                original.getAmplifier(),
                original.isAmbient(),
                original.isVisible(),
                original.showIcon()
        );
    }

    @Unique
    private static @Nullable ServerPlayer functionalTrims$asServerPlayer(
            Entity entity) {

        if (entity instanceof ServerPlayer player) {
            return player;
        }

        return null;
    }
}