package functional_trims.effect;

import functional_trims.config.ConfigManager;
import functional_trims.mixin.EntityAccessor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

/**
 * Manages the amethyst-trim glow / x-ray vision effect entirely server-side.
 * No longer extends MobEffect — that caused unmodded clients to be kicked
 * because Fabric syncs the mob_effect registry during login.
 */
public final class AmethystVisionEffect {

    private static final byte GLOW_MASK = 0x40;
    private static final int SCAN_INTERVAL_TICKS = 3;

    // Players whose vision is currently active
    private static final Set<UUID> VISION_ACTIVE = ConcurrentHashMap.newKeySet();

    // Per-player glow tracking (for unglow cleanup)
    private static final Map<UUID, Set<Integer>> glowingByPlayer = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean>      wasActive       = new ConcurrentHashMap<>();
    private static final Map<UUID, Long>          lastScanTick   = new ConcurrentHashMap<>();

    private AmethystVisionEffect() {}

    public static boolean isActive(UUID id) {
        return VISION_ACTIVE.contains(id);
    }

    /** Activate vision for a player (called from AmethystTrimEffect). */
    public static void activate(UUID id) {
        VISION_ACTIVE.add(id);
        wasActive.put(id, true);
    }

    /** Deactivate vision; the next tick() call will send unglow packets. */
    public static void deactivate(UUID id) {
        VISION_ACTIVE.remove(id);
    }

    /**
     * Called every END_LEVEL_TICK.
     * Scans for active players and handles unglow cleanup for players who just lost the effect.
     */
    public static void tick(ServerLevel world) {
        for (ServerPlayer player : world.players()) {
            UUID id = player.getUUID();
            boolean active = VISION_ACTIVE.contains(id);

            if (active) {
                wasActive.put(id, true);
                doScan(player, world);
            } else if (wasActive.getOrDefault(id, false)) {
                // Just went inactive — unglow everything this player sees
                Set<Integer> ids = glowingByPlayer.remove(id);
                if (ids != null) {
                    for (int eid : ids) {
                        var e = world.getEntity(eid);
                        if (e instanceof LivingEntity le) sendGlowPacket(player, le, false);
                    }
                }
                wasActive.put(id, false);
                lastScanTick.remove(id);
            }
        }
    }

    /** Called on player disconnect — removes all glow tracking state for that player. */
    public static void cleanupPlayer(UUID id) {
        VISION_ACTIVE.remove(id);
        glowingByPlayer.remove(id);
        wasActive.remove(id);
        lastScanTick.remove(id);
    }

    // ------------------------------------------------------------------

    private static void doScan(ServerPlayer player, ServerLevel world) {
        UUID id = player.getUUID();
        glowingByPlayer.putIfAbsent(id, ConcurrentHashMap.newKeySet());
        Set<Integer> glowingIds = glowingByPlayer.get(id);

        long gameTime = world.getGameTime();
        Long lastScan = lastScanTick.get(id);
        if (lastScan != null && gameTime - lastScan < SCAN_INTERVAL_TICKS) return;
        lastScanTick.put(id, gameTime);

        double radius = 25.0 * ConfigManager.get().amethyst.effectRangeMultiplier;
        var box = player.getBoundingBox().inflate(radius);
        var nearby = world.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive());
        Set<Integer> nearbyIds = new HashSet<>(nearby.size());
        for (LivingEntity living : nearby) nearbyIds.add(living.getId());

        // Remove out-of-range entities (unglow them)
        glowingIds.removeIf(eid -> {
            var e = world.getEntity(eid);
            if (!(e instanceof LivingEntity le)) return true;
            if (!nearbyIds.contains(eid)) {
                sendGlowPacket(player, le, false);
                return true;
            }
            return false;
        });

        // Add newly in-range entities
        for (var e : nearby) {
            if (glowingIds.add(e.getId())) {
                sendGlowPacket(player, e, true);
                if (e.isInvisible()) {
                    functional_trims.criteria.ModCriteria.TRIM_TRIGGER.trigger(player, "amethyst", "i_see_you");
                } else {
                    functional_trims.criteria.ModCriteria.TRIM_TRIGGER.trigger(player, "amethyst", "wallhacks_enabled");
                }
            }
        }
    }

    private static void sendGlowPacket(ServerPlayer player, LivingEntity target, boolean glow) {
        if (target == null || !target.isAlive()) return;
        var FLAGS = EntityAccessor.getFlags();
        byte serverFlags = target.getEntityData().get(FLAGS);
        byte clientFlags = glow ? (byte) (serverFlags | GLOW_MASK) : serverFlags;
        SynchedEntityData.DataValue<Byte> entry = SynchedEntityData.DataValue.create(FLAGS, clientFlags);
        player.connection.send(new ClientboundSetEntityDataPacket(target.getId(), List.of(entry)));
    }
}