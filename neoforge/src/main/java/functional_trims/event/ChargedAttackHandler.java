package functional_trims.event;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.effect.ModEffects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public final class ChargedAttackHandler {

    private ChargedAttackHandler() {
    }

    private static float boostedAttackMultiplier() {
        return ConfigManager.get().copper.chargedStrikeDamageMultiplier;
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ChargedAttackHandler::onAttackEntity);
    }

    private static void onAttackEntity(AttackEntityEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!(event.getTarget() instanceof LivingEntity target)) {
            return;
        }

        if (!player.hasEffect(ModEffects.CHARGED)) {
            return;
        }

        if (!FTConfig.isTrimEnabled("copper")) {
            return;
        }

        ServerLevel level = player.level();

        DamageSource attackSource =
                level.damageSources().playerAttack(player);

        double baseAttackDamage =
                player.getAttributeValue(Attributes.ATTACK_DAMAGE);

        float extraDamage =
                (float) (baseAttackDamage
                        * (boostedAttackMultiplier() - 1.0F));

        float itemBonusDamage =
                player.getMainHandItem()
                        .getItem()
                        .getAttackDamageBonus(
                                target,
                                (float) baseAttackDamage,
                                attackSource
                        );

        float resultingDamage =
                (float) (
                        (baseAttackDamage + itemBonusDamage)
                                * boostedAttackMultiplier()
                );

        LightningBolt lightning =
                new LightningBolt(
                        EntityType.LIGHTNING_BOLT,
                        level
                );

        lightning.setPos(
                target.getX()
                        + (level.getRandom().nextDouble() - 0.5) * 0.5,
                target.getY(),
                target.getZ()
                        + (level.getRandom().nextDouble() - 0.5) * 0.5
        );

        lightning.setVisualOnly(true);

        level.addFreshEntity(lightning);

        Vec3 knockback =
                target.position()
                        .subtract(player.position())
                        .normalize()
                        .scale(1.5);

        target.push(
                knockback.x,
                0.6,
                knockback.z
        );

        target.needsSync = true;

        target.hurtServer(
                level,
                attackSource,
                extraDamage
        );

        target.igniteForSeconds(4);

        if (player.getMainHandItem().is(Items.MACE)
                && resultingDamage >= 20.0F) {

            ModCriteria.TRIM_TRIGGER.get().trigger(
                    player,
                    "copper",
                    "mace_strike"
            );

            level.playSound(
                    null,
                    target.blockPosition(),
                    SoundEvents.TRIDENT_THUNDER.value(),
                    SoundSource.PLAYERS,
                    2.0F,
                    1.2F
            );
        }

        level.getServer().execute(
                () -> player.removeEffect(ModEffects.CHARGED)
        );

        level.playSound(
                null,
                target.blockPosition(),
                SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.PLAYERS,
                3.0F,
                0.8F
        );

        level.playSound(
                null,
                target.blockPosition(),
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.PLAYERS,
                2.5F,
                1.0F
        );

        event.setCanceled(true);
    }
}