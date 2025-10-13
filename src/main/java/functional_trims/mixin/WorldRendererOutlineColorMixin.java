package functional_trims.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import functional_trims.trim_effect.ModEffects;

/**
 * Overrides the default glowing color to purple when Amethyst Vision is active.
 */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererOutlineColorMixin {

    @WrapOperation(
            method = "renderEntities(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/RenderTickCounter;Ljava/util/List;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;setColor(IIII)V"
            )
    )
    private void functional_trims$forcePurpleOutline(
            OutlineVertexConsumerProvider provider,
            int r, int g, int b, int a,
            Operation<Void> original
    ) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client != null && client.player != null && hasAmethystVision(client)) {
            // purple = (170, 0, 255)
            provider.setColor(170, 0, 255, a);
            return;
        }

        original.call(provider, r, g, b, a);
    }

    private static boolean hasAmethystVision(MinecraftClient client) {
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            StatusEffect type = effect.getEffectType().value();
            if (type == ModEffects.AMETHYST_VISION) return true;
        }
        return false;
    }
}
