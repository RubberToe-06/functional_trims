package functional_trims.event;

import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public final class LapisTrimXpHandler {

    private static final float BONUS_MULTIPLIER = 0.5F;

    private LapisTrimXpHandler() {
    }

    public static void register() {

        NeoForge.EVENT_BUS.addListener(
                LapisTrimXpHandler::onPickupXp
        );
    }

    private static void onPickupXp(
            PlayerXpEvent.PickupXp event) {

        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        if (!FTConfig.isTrimEnabled("lapis")) {
            return;
        }

        if (!TrimHelper.hasFullTrim(
                player,
                TrimMaterials.LAPIS)) {
            return;
        }

        int bonus =
                Math.max(
                        1,
                        (int) (
                                event.getOrb().getValue()
                                        * BONUS_MULTIPLIER
                        )
                );

        player.giveExperiencePoints(
                bonus
        );

        ModCriteria.TRIM_TRIGGER
                .get()
                .trigger(
                        player,
                        "lapis",
                        "absorb_xp_orb"
                );

        if (player.experienceLevel >= 100) {

            ModCriteria.TRIM_TRIGGER
                    .get()
                    .trigger(
                            player,
                            "lapis",
                            "reach_level_100"
                    );
        }
    }
}