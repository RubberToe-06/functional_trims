package functional_trims.effect;

/**
 * Previously registered custom MobEffects (amethyst_vision, charged).
 * Those registrations were removed because Fabric syncs the mob_effect registry
 * to every connecting client, which kicked players who didn't have the mod installed.

 * The functionality is now provided by:
 *   - AmethystVisionEffect  (pure server-side state tracker)
 *   - ChargedState           (transient attribute modifiers, no registry entry)
 */
public final class ModEffects {
    private ModEffects() {}

    /** No-op — kept for source compatibility if anything still calls it. */
    public static void register() {}
}