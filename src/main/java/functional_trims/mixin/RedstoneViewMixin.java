package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.event.RedstoneTrimPowerTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EntityView;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneView.class)
public interface RedstoneViewMixin {

    @Inject(
            method = "isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private void functionalTrims$powerUnderTrimmedPlayers(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Object self = this;

        if (cir.getReturnValue()) return;
        if (!(self instanceof WorldView worldView)) return;
        if (worldView.isClient()) return;
        if (!(self instanceof EntityView entityView)) return;
        if (!FTConfig.isTrimEnabled("redstone")) return;

        for (PlayerEntity player : entityView.getPlayers()) {
            if (RedstoneTrimPowerTicker.isPlayerPoweringPos(player, pos)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Inject(
            method = "getReceivedStrongRedstonePower",
            at = @At("RETURN"),
            cancellable = true
    )
    private void functionalTrims$addTrimStrongPower(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!(this instanceof World world)) return;
        if (!FTConfig.isTrimEnabled("redstone")) return;

        int extraPower = Math.min(15, ConfigManager.get().blockPowerLevelWhenSteppedOn);

        for (PlayerEntity player : world.getPlayers()) {
            if (RedstoneTrimPowerTicker.isPlayerPoweringPos(player, pos)) {
                cir.setReturnValue(Math.max(cir.getReturnValue(), extraPower));
                return;
            }
        }
    }
}