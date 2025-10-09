package functional_trims.event;

import functional_trims.trim_effect.DiamondBurst;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import functional_trims.func.TrimHelper; // <-- adjust to your actual package

public final class DiamondTrimGuard {

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((LivingEntity living, net.minecraft.entity.damage.DamageSource source, float amount) -> {
            if (!(living instanceof ServerPlayerEntity player)) return true;

            // Require full set (4) of DIAMOND-trimmed pieces using your helper
            if (TrimHelper.countTrim(player, ArmorTrimMaterials.DIAMOND) != 4) return true;

            // Save from death: set to half max HP, but never below 1
            float half = player.getMaxHealth() * 0.5f;
            player.setHealth(Math.max(1.0f, half));

            // tiny grace so the same-tick multi-hit doesn’t finish them off immediately
            player.timeUntilRegen = Math.max(player.timeUntilRegen, 20);

            // Break ALL worn armor (treat as “completely breaks the armor”)
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack stack = player.getEquippedStack(slot);
                if (stack.isEmpty()) continue;

                if (stack.isDamageable()) {
                    // Deal exactly enough durability loss to break it this call
                    int toBreak = stack.getMaxDamage() - stack.getDamage();
                    if (toBreak > 0) {
                        stack.damage(toBreak, player, slot); // sends break status + decrements stack if broken
                    }
                } else {
                    // Non-damageable armor piece: fake the break and remove it
                    player.equipStack(slot, ItemStack.EMPTY);
                }
            }

            DiamondBurst.doBurst(player);

            // Cancel death
            return false;
        });
    }
}
