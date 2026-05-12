package functional_trims.effect;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Replaces the old ChargedException MobEffect.
 * Tracks charged players entirely server-side using transient attribute modifiers
 * (not saved to NBT, not synced as registry entries) so unmodded clients can connect.
 */
public final class ChargedState {

    // Remaining ticks for each charged player
    private static final Map<UUID, Integer> chargedTicks = new ConcurrentHashMap<>();

    private static final Identifier SPEED_ID =
            Identifier.fromNamespaceAndPath("functional_trims", "charged_speed");
    private static final Identifier ATTACK_SPEED_ID =
            Identifier.fromNamespaceAndPath("functional_trims", "charged_attack_speed");
    private static final Identifier BLOCK_BREAK_ID =
            Identifier.fromNamespaceAndPath("functional_trims", "charged_block_break");

    private ChargedState() {}

    public static boolean isCharged(ServerPlayer player) {
        return chargedTicks.containsKey(player.getUUID());
    }

    /**
     * Marks a player as charged for {@code durationTicks} ticks, applying attribute boosts.
     * If already charged, the duration is reset without re-adding modifiers.
     */
    public static void setCharged(ServerPlayer player, int durationTicks) {
        boolean wasCharged = chargedTicks.put(player.getUUID(), durationTicks) != null;
        if (!wasCharged) {
            applyModifiers(player);
        }
    }

    /** Immediately removes the charged state and strips attribute boosts. */
    public static void removeCharged(ServerPlayer player) {
        if (chargedTicks.remove(player.getUUID()) != null) {
            removeModifiers(player);
        }
    }

    /** Called every server tick to count down durations and expire effects. */
    public static void tick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID id = player.getUUID();
            Integer ticks = chargedTicks.get(id);
            if (ticks == null) continue;
            if (ticks <= 1) {
                chargedTicks.remove(id);
                removeModifiers(player);
            } else {
                chargedTicks.put(id, ticks - 1);
            }
        }
    }

    /**
     * Called on player disconnect. Removes from tracking; transient attribute modifiers
     * are automatically discarded when the entity is unloaded.
     */
    public static void cleanup(UUID id) {
        chargedTicks.remove(id);
    }

    // ------------------------------------------------------------------

    private static void applyModifiers(ServerPlayer player) {
        var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SPEED_ID);
            speedAttr.addTransientModifier(new AttributeModifier(
                    SPEED_ID, 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }

        var attackSpeedAttr = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttr != null) {
            attackSpeedAttr.removeModifier(ATTACK_SPEED_ID);
            attackSpeedAttr.addTransientModifier(new AttributeModifier(
                    ATTACK_SPEED_ID, 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }

        var blockBreakAttr = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (blockBreakAttr != null) {
            blockBreakAttr.removeModifier(BLOCK_BREAK_ID);
            blockBreakAttr.addTransientModifier(new AttributeModifier(
                    BLOCK_BREAK_ID, 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private static void removeModifiers(ServerPlayer player) {
        var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) speedAttr.removeModifier(SPEED_ID);

        var attackSpeedAttr = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttr != null) attackSpeedAttr.removeModifier(ATTACK_SPEED_ID);

        var blockBreakAttr = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (blockBreakAttr != null) blockBreakAttr.removeModifier(BLOCK_BREAK_ID);
    }
}

