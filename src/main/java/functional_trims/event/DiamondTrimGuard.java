package functional_trims.event;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;
import functional_trims.trim_effect.DiamondBurst;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;

public class DiamondTrimGuard {

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((LivingEntity living, net.minecraft.entity.damage.DamageSource source, float amount) -> {
            if (!(living instanceof ServerPlayerEntity player)) return true;
            if (!FTConfig.isTrimEnabled("diamond")) return true;
            if (TrimHelper.countTrim(player, ArmorTrimMaterials.DIAMOND) != 4) return true;

            savePlayerFromDeath(player);
            damageWornArmor(player);
            DiamondBurst.doBurst(player);

            return false;
        });
    }

    private static void savePlayerFromDeath(ServerPlayerEntity player) {
        float regainedHealth = Math.max(1.0f, player.getMaxHealth() * percentHealthRegained());
        player.setHealth(regainedHealth);

        // tiny grace so the same-tick multi-hit doesn’t finish them off immediately
        player.timeUntilRegen = Math.max(player.timeUntilRegen, 20);
    }

    private static void damageWornArmor(ServerPlayerEntity player) {
        float percent = Math.clamp(percentArmorDurabilityLost(), 0.0f, 1.0f);

        for (EquipmentSlot slot : new EquipmentSlot[] {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        }) {
            ItemStack stack = player.getEquippedStack(slot);
            if (stack.isEmpty()) continue;
            if (!stack.isDamageable()) continue;

            int durabilityLoss = Math.round(stack.getMaxDamage() * percent);
            if (durabilityLoss <= 0) continue;

            stack.damage(durabilityLoss, player, slot);
        }
    }

    private static float percentHealthRegained() {
        return ConfigManager.get().percentHealthRegainedAfterBurst;
    }

    private static float percentArmorDurabilityLost() {
        return ConfigManager.get().percentArmorDurabilityLostAfterBurst;
    }
}