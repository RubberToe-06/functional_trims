package functional_trims.mixin.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(
        targets = "alternate.current.wire.Node",
        remap = false
)
public interface AlternateCurrentNodeAccessor {

    @Accessor("pos")
    BlockPos functionalTrims$getPos();

    @Accessor("level")
    ServerLevel functionalTrims$getLevel();
}