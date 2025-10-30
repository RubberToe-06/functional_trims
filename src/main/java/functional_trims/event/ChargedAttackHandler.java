package functional_trims.event;

import functional_trims.criteria.ModCriteria;
import functional_trims.trim_effect.ModEffects;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public class ChargedAttackHandler {

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient()) return ActionResult.PASS;
            if (!(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.PASS;
            if (!(world instanceof ServerWorld serverWorld)) return ActionResult.PASS;
            if (!player.hasStatusEffect(ModEffects.CHARGED)) return ActionResult.PASS;
            if (!(entity instanceof LivingEntity target)) return ActionResult.PASS;

            // --- Base damage model ---
            double fall = Math.abs(player.fallDistance);
            float boostedDamage = (float)(7.5F + fall * 0.5F); // +0.5 per block fallen
            System.out.println("Effective Mace Smash Damage: " + boostedDamage);

            // --- Lightning strike ---
            LightningEntity lightning = new LightningEntity(net.minecraft.entity.EntityType.LIGHTNING_BOLT, serverWorld);
            lightning.refreshPositionAfterTeleport(
                    target.getX() + (world.getRandom().nextDouble() - 0.5) * 0.5,
                    target.getY(),
                    target.getZ() + (world.getRandom().nextDouble() - 0.5) * 0.5
            );
            lightning.setCosmetic(true);
            serverWorld.spawnEntity(lightning);

            // --- Knockback + Fire + Damage ---
            Vec3d knockback = target.getPos().subtract(player.getPos()).normalize().multiply(1.5);
            target.addVelocity(knockback.x, 0.6, knockback.z);
            target.velocityModified = true;

            target.damage(serverWorld, serverWorld.getDamageSources().playerAttack(player), boostedDamage);
            target.setOnFireFor(4);

            // ✅ Advancement trigger
            if (player.getMainHandStack().isOf(Items.MACE) && boostedDamage >= 20.0F) {
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

            // --- Remove Charged after hit ---
            serverWorld.getServer().execute(() -> player.removeStatusEffect(ModEffects.CHARGED));

            // --- Ambient effects ---
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
