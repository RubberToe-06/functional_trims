package functional_trims.event;

import functional_trims.trim_effect.ModEffects;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public class ChargedAttackHandler {
    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient()) return ActionResult.PASS;

            // Correct signature: RegistryEntry<StatusEffect>
            if (player.hasStatusEffect(ModEffects.CHARGED)) {
                if (entity instanceof LivingEntity target && world instanceof ServerWorld serverWorld) {

                    serverWorld.getServer().execute(() -> {
                        // Correct lightning constructor for 1.21.8: (EntityType, World)
                        LightningEntity lightning = new LightningEntity(net.minecraft.entity.EntityType.LIGHTNING_BOLT, serverWorld);
                        lightning.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
                        lightning.setCosmetic(true);
                        serverWorld.spawnEntity(lightning);

                        // ✅ Correct damage signature (world, source, amount)
                        target.damage(serverWorld, serverWorld.getDamageSources().playerAttack(player), 6.0F);
                        target.setOnFireFor(4);

                        // ✅ removeStatusEffect only takes 1 argument in your build
                        player.removeStatusEffect(ModEffects.CHARGED);

                        // Thunder feedback
                        serverWorld.playSound(
                                null,
                                target.getBlockPos(),
                                net.minecraft.sound.SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                                net.minecraft.sound.SoundCategory.PLAYERS,
                                2.0F,
                                1.0F
                        );
                    });
                }
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }
}
