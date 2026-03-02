package functional_trims.mixin;

import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EntityView;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes blocks under fully Redstone-trimmed players appear powered,
 * and triggers an advancement when they stand on a Redstone Lamp.
 */
@Mixin(RedstoneView.class)
public interface RedstoneView_RedstoneTrimPowerMixin {

    @Inject(
            method = "isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private void functionalTrims$powerUnderTrimmedPlayers(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Object self = this;
        // Only override if vanilla logic found no power
        if (cir.getReturnValue()) return;
        if (!(self instanceof WorldView worldView)) return;
        if (worldView.isClient()) return; // server only
        if (!(self instanceof EntityView entityView)) return;
        if (!FTConfig.isTrimEnabled("redstone")) return;

        for (PlayerEntity player : entityView.getPlayers()) {
            BlockPos playerFeet = player.getBlockPos();

            // Check if player is standing on this block
            if (playerFeet.down().equals(pos)) {
                int redstoneTrims = TrimHelper.countTrim(player, ArmorTrimMaterials.REDSTONE);
                if (redstoneTrims == 4) {
                    // Force this block to appear powered
                    cir.setReturnValue(true);

                    // Trigger advancement if it's a redstone lamp
                    if (worldView.getBlockState(pos).isOf(Blocks.REDSTONE_LAMP) && player instanceof ServerPlayerEntity serverPlayer) {
                        ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "redstone", "activate_lamp");
                    }

                    return;
                }
            }
        }
    }
}
