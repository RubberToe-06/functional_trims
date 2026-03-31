package functional_trims.mixin;

import functional_trims.FunctionalTrims;
import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Adds the Emerald trim bonus to loot tables
@Mixin(LootTable.class)
public abstract class LootTableMixin {
    @Unique private static final ThreadLocal<Boolean> emeraldTrim$rerolling = ThreadLocal.withInitial(() -> false);
    @Unique private static final float CHANCE_FOR_FIRST_REROLL = ConfigManager.get().emerald.percentChanceForExtraRoll1;
    @Unique private static final float CHANCE_FOR_SECOND_REROLL = ConfigManager.get().emerald.percentChanceForExtraRoll2;

    @Inject(
            method = "fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V",
            at = @At("TAIL")
    )
    private void emeraldTrimLuckyLoot(Container container, LootParams params, long optionalRandomSeed, CallbackInfo ci) {
        if (emeraldTrim$rerolling.get()) return; // prevent recursion

        Entity opener = params.contextMap().getOptional(LootContextParams.THIS_ENTITY);
        if (!(opener instanceof ServerPlayer player)) return;
        if (!FTConfig.isTrimEnabled("emerald")) return;

        int emeraldPieces = TrimHelper.countTrim(player, TrimMaterials.EMERALD);
        if (emeraldPieces != 4) return;

        emeraldTrim$rerolling.set(true);
        int extraRolls = 0;
        try {
            RandomSource random = params.getLevel().getRandom();
            if (random.nextFloat() < CHANCE_FOR_FIRST_REROLL) extraRolls++;
            if (random.nextFloat() < CHANCE_FOR_SECOND_REROLL) extraRolls++;

            for (int i = 0; i < extraRolls; i++) {
                ((LootTable) (Object) this).fill(container, params, optionalRandomSeed + i + 1);
            }

        } finally {
            emeraldTrim$rerolling.set(false);
            FunctionalTrims.LOGGER.info("Player {} with {} emerald trims got {} extra loot rolls", player.getName().getString(), emeraldPieces, extraRolls);
            ModCriteria.TRIM_TRIGGER.trigger(player, "emerald", "open_loot_chest");
        }
    }
}