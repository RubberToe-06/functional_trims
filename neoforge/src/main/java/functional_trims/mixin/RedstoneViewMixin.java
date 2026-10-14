package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.event.RedstoneTrimPowerTicker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
    private void functionalTrims$powerUnderTrimmedPlayers(
            BlockPos pos,
            CallbackInfoReturnable<Boolean> cir) {

        if (cir.getReturnValue()) {
            return;
        }

        Object self = this;

        if (!(self instanceof Level level)) {
            return;
        }

        if (level.isClientSide()) {
            return;
        }

        if (!FTConfig.isTrimEnabled("redstone")) {
            return;
        }

        for (Player player : level.players()) {

            if (RedstoneTrimPowerTicker.isPlayerPoweringPos(
                    player,
                    pos
            )) {

                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Inject(
            method = "getDirectSignalTo(Lnet/minecraft/core/BlockPos;)I",
            at = @At("RETURN"),
            cancellable = true
    )
    private void functionalTrims$addTrimStrongPower(
            BlockPos pos,
            CallbackInfoReturnable<Integer> cir) {

        Object self = this;

        if (!(self instanceof Level level)) {
            return;
        }

        if (!FTConfig.isTrimEnabled("redstone")) {
            return;
        }

        int extraPower =
                Math.min(
                        15,
                        ConfigManager.get()
                                .redstone
                                .blockPowerLevelWhenSteppedOn
                );

        for (Player player : level.players()) {

            if (RedstoneTrimPowerTicker.isPlayerPoweringPos(
                    player,
                    pos
            )) {

                cir.setReturnValue(
                        Math.max(
                                cir.getReturnValue(),
                                extraPower
                        )
                );

                return;
            }
        }
    }
}