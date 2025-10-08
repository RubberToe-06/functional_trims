package functional_trims.mixin;

import functional_trims.FunctionalTrims;
import functional_trims.func.TrimHelper;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

// Adds the Emerald trim bonus to loot tables
@Mixin(LootTable.class)
public abstract class LootTableMixin {
    private static final ThreadLocal<Boolean> emeraldTrim$rerolling = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "supplyInventory(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/loot/context/LootWorldContext;J)V",
            at = @At("TAIL")
    )
    private void emeraldTrimLuckyLoot(Inventory inventory, LootWorldContext ctx, long seed, CallbackInfo ci) {
        if (emeraldTrim$rerolling.get()) return; // prevent recursion

        Entity opener = ctx.getParameters().getNullable(LootContextParameters.THIS_ENTITY);
        if (!(opener instanceof ServerPlayerEntity player)) return;

        int emeraldPieces = TrimHelper.countTrim(player, ArmorTrimMaterials.EMERALD);
        if (emeraldPieces != 4) return;

        emeraldTrim$rerolling.set(true);
        int extraRolls = 0;
        try {
            Random random = ctx.getWorld().getRandom();
            extraRolls = 0;

            switch (emeraldPieces) {
                case 4 -> { // Guaranteed +1, 50% chance for a 2nd
                    extraRolls = 1;
                    if (random.nextFloat() < 0.50f) extraRolls++;
                }
            }


            for (int i = 0; i < extraRolls; i++) {
                ((LootTable) (Object) this).supplyInventory(inventory, ctx, seed + i + 1);
            }

        } finally {
            emeraldTrim$rerolling.set(false);
            FunctionalTrims.LOGGER.info("Player {} with {} emerald trims got {} extra loot rolls",
                    player.getName().getString(), emeraldPieces, extraRolls);
        }
    }
}









