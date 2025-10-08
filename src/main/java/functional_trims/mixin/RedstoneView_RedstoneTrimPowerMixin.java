package functional_trims.mixin;

import functional_trims.func.TrimHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EntityView;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes blocks under trimmed players appear powered, even if vanilla logic says otherwise.
 */
@Mixin(RedstoneView.class)
public interface RedstoneView_RedstoneTrimPowerMixin {

    @Inject(
            method = "isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private void functionalTrims$powerUnderTrimmedPlayers(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;

        Object self = this;
        if (!(self instanceof WorldView worldView)) return;
        if (worldView.isClient()) return;
        if (!(self instanceof EntityView entityView)) return;

        for (PlayerEntity player : entityView.getPlayers()) {
            BlockPos playerFeet = player.getBlockPos();
            if (playerFeet.down().equals(pos)) {
                int redstoneTrims = TrimHelper.countTrim(player, net.minecraft.item.equipment.trim.ArmorTrimMaterials.REDSTONE);
                if (redstoneTrims == 4) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}
