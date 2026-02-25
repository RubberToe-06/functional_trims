package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.RedstoneView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneView.class)
public interface RedstoneView_StrongPowerMixin {

    @Inject(method = "getReceivedStrongRedstonePower", at = @At("RETURN"), cancellable = true)
    private void functionalTrims$addTrimStrongPower(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!(this instanceof World world)) return;
        if (!FTConfig.isTrimEnabled("redstone")) return;
        final int EXTRA_POWER = ConfigManager.get().blockPowerLevelWhenSteppedOn;
        // Check for players above the position
        for (PlayerEntity player : world.getPlayers()) {
            if (player.getBlockPos().down().equals(pos)) {
                int trimCount = TrimHelper.countTrim(player, ArmorTrimMaterials.REDSTONE);
                if (trimCount == 4) {
                    // Convert trim count to redstone power strength (1–15)
                    int extraPower = Math.min(15, EXTRA_POWER);
                    cir.setReturnValue(Math.max(cir.getReturnValue(), extraPower));
                    return;
                }
            }
        }
    }
}
