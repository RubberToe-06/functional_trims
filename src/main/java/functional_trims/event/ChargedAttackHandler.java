package functional_trims.event;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.effect.ModEffects;
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

public class ChargedAttackHandler {

    private static float boostedAttackMultiplier() {
        return ConfigManager.get().copper.chargedStrikeDamageMultiplier;
    }

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, _, entity, _) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
            if (!(world instanceof ServerLevel serverWorld)) return InteractionResult.PASS;
            if (!(entity instanceof LivingEntity target)) return InteractionResult.PASS;
            if (!player.hasEffect(ModEffects.CHARGED)) return InteractionResult.PASS;
            if (!FTConfig.isTrimEnabled("copper")) return InteractionResult.PASS;

            double baseAttackDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float extraDamage = (float) (baseAttackDamage * (boostedAttackMultiplier() - 1.0F));
            float resultingDamage = (float) (baseAttackDamage * boostedAttackMultiplier());

            LightningBolt lightning = new LightningBolt(net.minecraft.world.entity.EntityType.LIGHTNING_BOLT, serverWorld);
            lightning.snapTo(
                    target.getX() + (world.getRandom().nextDouble() - 0.5) * 0.5,
                    target.getY(),
                    target.getZ() + (world.getRandom().nextDouble() - 0.5) * 0.5
            );
            lightning.setVisualOnly(true);
            serverWorld.addFreshEntity(lightning);

            Vec3 knockback = target.position().subtract(player.position()).normalize().scale(1.5);
            target.push(knockback.x, 0.6, knockback.z);
            target.needsSync = true;

            target.hurtServer(
                    serverWorld,
                    serverWorld.damageSources().playerAttack(player),
                    extraDamage
            );
            target.igniteForSeconds(4);

            if (player.getMainHandItem().is(Items.MACE) && resultingDamage >= 20.0F) {
                ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "copper", "mace_strike");

                serverWorld.playSound(
                        null,
                        target.blockPosition(),
                        SoundEvents.TRIDENT_THUNDER.value(),
                        SoundSource.PLAYERS,
                        2.0F,
                        1.2F
                );
            }

            System.out.println("Total Damage: " + resultingDamage);
            serverWorld.getServer().execute(() -> player.removeEffect(ModEffects.CHARGED));

            serverWorld.playSound(
                    null,
                    target.blockPosition(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER,
                    SoundSource.PLAYERS,
                    3.0F,
                    0.8F
            );
            serverWorld.playSound(
                    null,
                    target.blockPosition(),
                    SoundEvents.GENERIC_EXPLODE.value(),
                    SoundSource.PLAYERS,
                    2.5F,
                    1.0F
            );

            return InteractionResult.SUCCESS;
        });
    }
}