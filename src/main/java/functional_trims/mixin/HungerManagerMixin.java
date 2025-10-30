package functional_trims.mixin;

import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    private static final float FOOD_HUNGER_MULT = 1.25f; // +25% hunger (nutrition)
    private static final float FOOD_SAT_MULT    = 1.25f; // +25% saturation

    @Unique private @Nullable ServerPlayerEntity functionalTrims$owner;

    /** Cache the owning player each tick (valid in 1.21.8). */
    @Inject(method = "update", at = @At("HEAD"))
    private void functionalTrims$setOwner(ServerPlayerEntity player, CallbackInfo ci) {
        this.functionalTrims$owner = player;
    }

    /**
     * Scale the nutrition/saturation inside HungerManager#eat(FoodComponent).
     */
    @ModifyArgs(
            method = "eat(Lnet/minecraft/component/type/FoodComponent;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/HungerManager;addInternal(IF)V")
    )
    private void functionalTrims$quartz_boostFood(Args args) {
        if (functionalTrims$owner == null) return;
        if (!(TrimHelper.countTrim(functionalTrims$owner, ArmorTrimMaterials.QUARTZ) == 4)) return;

        // Check what item the player is eating
        ItemStack using = functionalTrims$owner.isUsingItem()
                ? functionalTrims$owner.getActiveItem()
                : ItemStack.EMPTY;
        Item item = using.getItem();

        // Skip special foods
        if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE || item == Items.SUSPICIOUS_STEW) return;

        // Boost hunger and saturation
        int   nutrition  = (Integer) args.get(0);
        float saturation = (Float)   args.get(1);

        int   boostedNutrition  = Math.max(Math.round(nutrition * FOOD_HUNGER_MULT), 2);
        float boostedSaturation = saturation * FOOD_SAT_MULT;

        args.set(0, boostedNutrition);
        args.set(1, boostedSaturation);

        // --- Advancement trigger: "eat_golden_carrot" ---
        if (item == Items.GOLDEN_CARROT) {
            ModCriteria.TRIM_TRIGGER.trigger(functionalTrims$owner, "quartz", "eat_golden_carrot");
        }
    }
}
