package functional_trims.event;

import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class GoldTrimAttackListener {
    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient
                    && entity instanceof PiglinBruteEntity
                    && player instanceof ServerPlayerEntity serverPlayer
                    && TrimHelper.countTrim(player, ArmorTrimMaterials.GOLD) == 4) {

                ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "gold", "attack_piglin_brute");
            }
            return ActionResult.PASS;
        });
    }
}
