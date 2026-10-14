package functional_trims.event;

import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent;

import java.util.Iterator;

public final class NetheriteExplosionHandler {

    private NetheriteExplosionHandler() {
    }

    public static void register() {

        NeoForge.EVENT_BUS.addListener(
                NetheriteExplosionHandler::onExplosionDetonate
        );
    }

    private static void onExplosionDetonate(
            ExplosionEvent.Detonate event) {

        if (!FTConfig.isTrimEnabled("netherite")) {
            return;
        }

        Iterator<Entity> iterator =
                event.getAffectedEntities().iterator();

        while (iterator.hasNext()) {

            Entity entity =
                    iterator.next();

            if (!(entity
                    instanceof ServerPlayer player)) {
                continue;
            }

            if (!TrimHelper.hasFullTrim(
                    player,
                    TrimMaterials.NETHERITE)) {
                continue;
            }

            iterator.remove();

            ModCriteria.TRIM_TRIGGER
                    .get()
                    .trigger(
                            player,
                            "netherite",
                            "resist_explosion"
                    );

            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.NETHERITE_BLOCK_STEP,
                    SoundSource.PLAYERS,
                    0.7F,
                    0.5F
            );
        }
    }
}