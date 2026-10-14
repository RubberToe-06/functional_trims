package functional_trims.mixin.compat;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.event.RedstoneTrimPowerTicker;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        targets = "alternate.current.wire.WireHandler",
        remap = false
)
public abstract class AlternateCurrentWireHandlerMixin {

    @Inject(
            method = "getDirectSignalTo(Lalternate/current/wire/WireNode;Lalternate/current/wire/Node;)I",
            at = @At("RETURN"),
            cancellable = true,
            require = 0,
            remap = false
    )
    private void functionalTrims$addRedstoneTrimPower(
            @Coerce Object wire,
            @Coerce Object node,
            CallbackInfoReturnable<Integer> cir) {

        if (wire == null || node == null) {
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

        if (extraPower <= cir.getReturnValue()) {
            return;
        }

        AlternateCurrentNodeAccessor nodeAccessor =
                (AlternateCurrentNodeAccessor) node;

        BlockPos pos =
                nodeAccessor.functionalTrims$getPos();

        ServerLevel level =
                nodeAccessor.functionalTrims$getLevel();

        for (Player player : level.players()) {

            if (RedstoneTrimPowerTicker.isPlayerPoweringPos(
                    player,
                    pos
            )) {

                cir.setReturnValue(extraPower);
                return;
            }
        }
    }
}