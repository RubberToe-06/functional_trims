package functional_trims.event;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.effect.ModEffects;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public class ChargedAttackHandler {

    private static float boostedAttackMultiplier() {
        return ConfigManager.get().copper.chargedStrikeDamageMultiplier;
    }

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient()) return ActionResult.PASS;
            if (!(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.PASS;
            if (!(world instanceof ServerWorld serverWorld)) return ActionResult.PASS;
            if (!(entity instanceof LivingEntity target)) return ActionResult.PASS;
            if (!player.hasStatusEffect(ModEffects.CHARGED)) return ActionResult.PASS;
            if (!FTConfig.isTrimEnabled("copper")) return ActionResult.PASS;

            double baseAttackDamage = player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
            float extraDamage = (float) (baseAttackDamage * (boostedAttackMultiplier() - 1.0F));
            float resultingDamage = (float) (baseAttackDamage * boostedAttackMultiplier());

            LightningEntity lightning = new LightningEntity(net.minecraft.entity.EntityType.LIGHTNING_BOLT, serverWorld);
            lightning.refreshPositionAfterTeleport(
                    target.getX() + (world.getRandom().nextDouble() - 0.5) * 0.5,
                    target.getY(),
                    target.getZ() + (world.getRandom().nextDouble() - 0.5) * 0.5
            );
            lightning.setCosmetic(true);
            serverWorld.spawnEntity(lightning);

            Vec3d knockback = target.getEntityPos().subtract(player.getEntityPos()).normalize().multiply(1.5);
            target.addVelocity(knockback.x, 0.6, knockback.z);
            target.velocityDirty = true;

            target.damage(
                    serverWorld,
                    serverWorld.getDamageSources().playerAttack(player),
                    extraDamage
            );
            target.setOnFireFor(4);

            if (player.getMainHandStack().isOf(Items.MACE) && resultingDamage >= 20.0F) {
                ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "copper", "mace_strike");

                serverWorld.playSound(
                        null,
                        target.getBlockPos(),
                        SoundEvents.ITEM_TRIDENT_THUNDER.value(),
                        SoundCategory.PLAYERS,
                        2.0F,
                        1.2F
                );
            }

            assert serverWorld.getServer() != null;
            System.out.println("Total Damage: " + resultingDamage);
            serverWorld.getServer().execute(() -> player.removeStatusEffect(ModEffects.CHARGED));

            serverWorld.playSound(
                    null,
                    target.getBlockPos(),
                    SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                    SoundCategory.PLAYERS,
                    3.0F,
                    0.8F
            );
            serverWorld.playSound(
                    null,
                    target.getBlockPos(),
                    SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
                    SoundCategory.PLAYERS,
                    2.5F,
                    1.0F
            );

            return ActionResult.SUCCESS;
        });
    }
}