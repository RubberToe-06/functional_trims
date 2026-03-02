package functional_trims.event;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.trim_effect.DiamondBurst;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import functional_trims.func.TrimHelper; // <-- adjust to your actual package

public final class DiamondTrimGuard {
    private static final float PERCENT_HEALTH_REGAINED = ConfigManager.get().percentHealthRegainedAfterBurst;
    private static final float PERCENT_ARMOR_DURABILITY_LOST = ConfigManager.get().percentArmorDurabilityLostAfterBurst;
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((LivingEntity living, net.minecraft.entity.damage.DamageSource source, float amount) -> {
            if (!(living instanceof ServerPlayerEntity player)) return true;
            if (!FTConfig.isTrimEnabled("diamond")) return true;

            // Require full set (4) of DIAMOND-trimmed pieces using your helper
            if (TrimHelper.countTrim(player, ArmorTrimMaterials.DIAMOND) != 4) return true;

            // Save from death: set to half max HP, but never below 1
            float half = player.getMaxHealth() * PERCENT_HEALTH_REGAINED;
            player.setHealth(Math.max(1.0f, half));

            // tiny grace so the same-tick multi-hit doesn’t finish them off immediately
            player.timeUntilRegen = Math.max(player.timeUntilRegen, 20);

            // Break ALL worn armor (treat as “completely breaks the armor”)
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack stack = player.getEquippedStack(slot);
                if (stack.isEmpty()) continue;
                if (!stack.isDamageable()) continue;

                int maxDurability = stack.getMaxDamage();

                // Clamp percent between 0 and 1 for safety
                float percent = Math.max(0.0f,
                        Math.min(1.0f, PERCENT_ARMOR_DURABILITY_LOST));

                int durabilityLoss = Math.round(maxDurability * percent);

                if (durabilityLoss <= 0) continue;

                stack.damage(durabilityLoss, player, slot);
            }

            DiamondBurst.doBurst(player);

            // Cancel death
            return false;
        });
    }
}
