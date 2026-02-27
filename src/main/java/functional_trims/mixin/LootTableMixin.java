package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Adds the Emerald trim bonus to loot tables
@Mixin(LootTable.class)
public abstract class LootTableMixin {
    private static final ThreadLocal<Boolean> emeraldTrim$rerolling = ThreadLocal.withInitial(() -> false);
    private static final float CHANCE_FOR_FIRST_REROLL = ConfigManager.get().percentChanceForExtraRoll1;
    private static final float CHANCE_FOR_SECOND_REROLL = ConfigManager.get().percentChanceForExtraRoll2;

    @Inject(
            method = "supplyInventory(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/loot/context/LootContextParameterSet;J)V",
            at = @At("TAIL")
    )
    private void emeraldTrimLuckyLoot(Inventory inventory, LootContextParameterSet params, long seed, CallbackInfo ci) {
        if (emeraldTrim$rerolling.get()) return;

        Entity opener = params.get(LootContextParameters.THIS_ENTITY);
        if (!(opener instanceof ServerPlayerEntity player)) return;
        if (!FTConfig.isTrimEnabled("emerald")) return;
        if (TrimHelper.countTrim(player, ArmorTrimMaterials.EMERALD) != 4) return;

        emeraldTrim$rerolling.set(true);
        int extraRolls = 0;
        try {
            Random random = params.getWorld().getRandom();
            if (random.nextFloat() < CHANCE_FOR_FIRST_REROLL) extraRolls++;
            if (random.nextFloat() < CHANCE_FOR_SECOND_REROLL) extraRolls++;

            for (int i = 0; i < extraRolls; i++) {
                ((LootTable)(Object)this).supplyInventory(inventory, params, seed + i + 1);
            }
        } finally {
            emeraldTrim$rerolling.set(false);
            ModCriteria.TRIM_TRIGGER.trigger(player, "emerald", "open_loot_chest");
        }
    }
}









