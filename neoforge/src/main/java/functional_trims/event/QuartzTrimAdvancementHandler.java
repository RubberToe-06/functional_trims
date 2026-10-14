package functional_trims.event;

import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public final class QuartzTrimAdvancementHandler {

    private QuartzTrimAdvancementHandler() {
    }

    public static void register() {

        NeoForge.EVENT_BUS.addListener(
                QuartzTrimAdvancementHandler::onItemFinished
        );
    }

    private static void onItemFinished(
            LivingEntityUseItemEvent.Finish event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!FTConfig.isTrimEnabled("quartz")) {
            return;
        }

        if (TrimHelper.countTrim(
                player,
                TrimMaterials.QUARTZ
        ) != 4) {
            return;
        }

        if (!event.getItem().is(Items.GOLDEN_CARROT)) {
            return;
        }

        ModCriteria.TRIM_TRIGGER
                .get()
                .trigger(
                        player,
                        "quartz",
                        "eat_golden_carrot"
                );
    }
}