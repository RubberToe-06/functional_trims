package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.event.RedstoneTrimPowerTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignalGetter.class)
public interface RedstoneViewMixin {

    @Inject(
            method = "hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private void functionalTrims$powerUnderTrimmedPlayers(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Object self = this;

        if (cir.getReturnValue()) return;
        if (!(self instanceof LevelReader worldView)) return;
        if (worldView.isClientSide()) return;
        if (!(self instanceof EntityGetter entityView)) return;
        if (!FTConfig.isTrimEnabled("redstone")) return;

        for (Player player : entityView.players()) {
            if (RedstoneTrimPowerTicker.isPlayerPoweringPos(player, pos)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Inject(
            method = "getDirectSignalTo",
            at = @At("RETURN"),
            cancellable = true
    )
    private void functionalTrims$addTrimStrongPower(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!(this instanceof Level world)) return;
        if (!FTConfig.isTrimEnabled("redstone")) return;

        int extraPower = Math.min(15, ConfigManager.get().redstone.blockPowerLevelWhenSteppedOn);

        for (Player player : world.players()) {
            if (RedstoneTrimPowerTicker.isPlayerPoweringPos(player, pos)) {
                cir.setReturnValue(Math.max(cir.getReturnValue(), extraPower));
                return;
            }
        }
    }
}