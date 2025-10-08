package functional_trims.mixin;

import functional_trims.FunctionalTrims;
import functional_trims.func.TrimHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Adds the Lapis trim bonus to experience orbs
@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {
    @Inject(
            method = "onPlayerCollision",
            at = @At("HEAD")
    )
    private void boostExp(PlayerEntity player, CallbackInfo ci) {
        // Check if player has any Lapis-trimmed armor
        int trimCount = TrimHelper.countTrim(player, ArmorTrimMaterials.LAPIS);
        if (trimCount == 4) {
            // 'this' is the orb being collided with
            ExperienceOrbEntity orb = (ExperienceOrbEntity) (Object) this;

            // Give extra XP
            float  bonusMultiplier = (4 * 0.125f);
            FunctionalTrims.LOGGER.info("bonusMultiplier: {}", bonusMultiplier);
            int bonus = (int) Math.max(1, (orb.getValue() * bonusMultiplier));
            player.addExperience(bonus);
        }
    }
}

