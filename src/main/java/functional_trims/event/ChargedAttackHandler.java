package functional_trims.event;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.effect.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ChargedAttackHandler {
    private static final Set<UUID> PENDING_CHARGED_MACE_STRIKES = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> APPLYING_BONUS_DAMAGE = ConcurrentHashMap.newKeySet();

    private ChargedAttackHandler() {
    }

    private static boolean isCopperDisabled() {
        return !FTConfig.isTrimEnabled("copper");
    }

    private static boolean isChargedMaceAttack(ServerPlayer player) {
        return player.hasEffect(ModEffects.CHARGED) && player.getMainHandItem().is(Items.MACE);
    }

    private static float getExtraDamage(ServerPlayer player) {
        double baseAttackDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        return (float) (baseAttackDamage * (ConfigManager.get().copper.chargedStrikeDamageMultiplier - 1.0F));
    }

    private static void playThunderRewardSound(ServerLevel level, LivingEntity target) {
        level.playSound(
                null,
                target.blockPosition(),
                SoundEvents.TRIDENT_THUNDER.value(),
                SoundSource.PLAYERS,
                2.0F,
                1.2F
        );
    }

    private static void triggerMaceAdvancement(ServerPlayer player, LivingEntity target) {
        ModCriteria.TRIM_TRIGGER.trigger(player, "copper", "mace_strike");
        playThunderRewardSound(player.level(), target);
    }

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, _, entity, _) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
            if (!(world instanceof ServerLevel serverLevel)) return InteractionResult.PASS;
            if (!(entity instanceof LivingEntity target)) return InteractionResult.PASS;
            if (!player.hasEffect(ModEffects.CHARGED)) return InteractionResult.PASS;
            if (isCopperDisabled()) return InteractionResult.PASS;

            if (serverPlayer.getMainHandItem().is(Items.MACE)) {
                PENDING_CHARGED_MACE_STRIKES.add(serverPlayer.getUUID());
            }

            LightningBolt lightning = new LightningBolt(net.minecraft.world.entity.EntityType.LIGHTNING_BOLT, serverLevel);
            lightning.snapTo(
                    target.getX() + (world.getRandom().nextDouble() - 0.5) * 0.5,
                    target.getY(),
                    target.getZ() + (world.getRandom().nextDouble() - 0.5) * 0.5
            );
            lightning.setVisualOnly(true);
            serverLevel.addFreshEntity(lightning);

            Vec3 delta = target.position().subtract(player.position());
            if (delta.lengthSqr() > 1.0E-6) {
                Vec3 knockback = delta.normalize().scale(1.5);
                target.push(knockback.x, 0.6, knockback.z);
                target.hurtMarked = true;
            }

            target.igniteForSeconds(4);

            serverLevel.playSound(
                    null,
                    target.blockPosition(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER,
                    SoundSource.PLAYERS,
                    3.0F,
                    0.8F
            );
            serverLevel.playSound(
                    null,
                    target.blockPosition(),
                    SoundEvents.GENERIC_EXPLODE.value(),
                    SoundSource.PLAYERS,
                    2.5F,
                    1.0F
            );

            return InteractionResult.PASS;
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, _, _) -> {
            if (!(source.getEntity() instanceof ServerPlayer serverPlayer)) return;
            if (isCopperDisabled()) return;
            if (!serverPlayer.getMainHandItem().is(Items.MACE)) return;

            UUID playerId = serverPlayer.getUUID();

            if (APPLYING_BONUS_DAMAGE.remove(playerId)) return;
            if (!PENDING_CHARGED_MACE_STRIKES.remove(playerId)) return;

            if (baseDamageTaken >= 20.0F) {
                triggerMaceAdvancement(serverPlayer, entity);
            }

            APPLYING_BONUS_DAMAGE.add(playerId);
            entity.hurtServer(
                    serverPlayer.level(),
                    serverPlayer.level().damageSources().playerAttack(serverPlayer),
                    getExtraDamage(serverPlayer)
            );

            serverPlayer.removeEffect(ModEffects.CHARGED);
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!(source.getEntity() instanceof ServerPlayer serverPlayer)) return;
            if (isCopperDisabled()) return;
            if (!isChargedMaceAttack(serverPlayer)) return;
            if (!PENDING_CHARGED_MACE_STRIKES.remove(serverPlayer.getUUID())) return;

            triggerMaceAdvancement(serverPlayer, entity);
            serverPlayer.removeEffect(ModEffects.CHARGED);
        });
    }
}