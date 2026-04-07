package functional_trims.event;

import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

public class GoldTrimAttackListener {
    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, _, entity, _) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!FTConfig.isTrimEnabled("gold")) return InteractionResult.PASS;
            if (!(entity instanceof PiglinBrute)) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
            if (TrimHelper.countTrim(player, TrimMaterials.GOLD) != 4) return InteractionResult.PASS;

            ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "gold", "attack_piglin_brute");
            return InteractionResult.PASS;
        });
    }
}