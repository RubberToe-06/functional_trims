package functional_trims.event;

import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;

public final class NetheriteKnockbackHandler {

    private NetheriteKnockbackHandler() {
    }

    public static void register() {

        NeoForge.EVENT_BUS.addListener(
                NetheriteKnockbackHandler::onKnockback
        );
    }

    private static void onKnockback(
            LivingKnockBackEvent event) {

        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        if (!FTConfig.isTrimEnabled("netherite")) {
            return;
        }

        if (!TrimHelper.hasFullTrim(
                player,
                TrimMaterials.NETHERITE)) {
            return;
        }

        event.setCanceled(true);
    }
}