package functional_trims.mixin;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FoodData.class)
public abstract class HungerManagerMixin {
    @Unique private @Nullable ServerPlayer functionalTrims$owner;

    @Inject(method = "tick", at = @At("HEAD"))
    private void functionalTrims$setOwner(ServerPlayer player, CallbackInfo ci) {
        this.functionalTrims$owner = player;
    }

    @ModifyArgs(
            method = "eat(Lnet/minecraft/world/food/FoodProperties;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodData;add(IF)V"
            )
    )
    private void functionalTrims$quartz_boostFood(Args args) {
        if (functionalTrims$owner == null) return;
        if (TrimHelper.countTrim(functionalTrims$owner, TrimMaterials.QUARTZ) != 4) return;
        if (!FTConfig.isTrimEnabled("quartz")) return;

        ItemStack using = functionalTrims$owner.isUsingItem()
                ? functionalTrims$owner.getUseItem()
                : ItemStack.EMPTY;
        Item item = using.getItem();

        if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE || item == Items.SUSPICIOUS_STEW) return;

        float hungerMult = ConfigManager.get().quartz.hungerRestoredMultiplier;
        float satMult = ConfigManager.get().quartz.saturationRestoredMultiplier;

        int nutrition = args.get(0);
        float saturation = args.get(1);

        args.set(0, Math.max(Math.round(nutrition * hungerMult), 2));
        args.set(1, saturation * satMult);

        if (item == Items.GOLDEN_CARROT) {
            ModCriteria.TRIM_TRIGGER.trigger(functionalTrims$owner, "quartz", "eat_golden_carrot");
        }
    }
}