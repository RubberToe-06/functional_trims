# AGENTS.md

## What this project is
- `functional_trims` is a Fabric mod that turns full armor trim sets into gameplay effects; entrypoint is `src/main/java/functional_trims/FunctionalTrims.java`.
- Core flow: load config -> register effects/criteria/events -> tick handlers evaluate full-set trim state and apply behavior.
- Shared trim check logic lives in `src/main/java/functional_trims/func/TrimHelper.java` (`countTrim` / `hasFullTrim`), and most gameplay code assumes `count == 4` for activation.

## Minecraft versioning and mapping system

### New version numbering (post-1.x era)
Mojang switched from the old `1.X.Y` scheme to a **year-based** scheme starting in 2025:

| Format            | Meaning                                | Example                              |
|-------------------|----------------------------------------|--------------------------------------|
| `YY.N`            | Year (2 digits) + major release number | `26.1` = first major release of 2026 |
| `YY.N.P`          | Patch release                          | `26.1.1`                             |
| `YY.N-rc-X`       | Release candidate                      | `26.1-rc-3`                          |
| `YY.N-pre-X`      | Pre-release                            | `26.1-pre-2`                         |
| `YY.N-snapshot-X` | Numbered snapshot                      | `26.1-snapshot-11`                   |
| `YYwWWa`          | Old-style week snapshot (still used)   | `26w14a`                             |

This project targets **`26.1`** (set in `gradle.properties` → `minecraft_version=26.1`).

### Mojang now ships unobfuscated code — yarn is obsolete
Starting with this version era, **Mojang publishes fully human-readable ("mojmap") names in the production JAR** — no more obfuscated `a`, `b`, `c` class/field/method names. Practical consequences:

- **Yarn mappings are no longer needed or maintained** for these versions. Do not add or reference yarn dependencies.
- **All source lookups, mixins, and AW entries use mojmap names** (the same names Mojang chose, e.g. `net.minecraft.world.entity.Entity`, `DATA_SHARED_FLAGS_ID`).
- **`find_mapping`** (the MCP symbol translator) is a no-op for 26.x — passing `sourceMapping=mojmap` will error because there is no translation layer.
- Fabric Loom still uses an `intermediary` layer internally for stable remapping, but you never write or read intermediary names directly when working on this mod.
- The project's `functional_trims.accesswidener` was written against old Yarn names and declares namespace `official` — this is a known inconsistency flagged by `validate_access_widener`. Any AW edits should use the current mojmap class path (e.g. `net/minecraft/server/level/ServerPlayer`).

## Architecture map (read these first)
- `src/main/java/functional_trims/FunctionalTrims.java`: bootstrap and registration order.
- `src/main/java/functional_trims/config/`: JSON config model + Cloth Config screen + Mod Menu hook.
- `src/main/java/functional_trims/trim_effect/`: standalone trim behaviors (often event/tick registration per effect).
- `src/main/java/functional_trims/event/`: global listeners and tickers (advancement grants, charged attacks, redstone powering).
- `src/main/java/functional_trims/mixin/`: vanilla behavior patches for effects that cannot be done through API events.
- `src/main/java/functional_trims/criteria/` + `src/main/java/functional_trims/datagen/TrimAdvancementProvider.java`: custom advancement trigger plumbing + generated advancement graph.

## Build and dev workflows (verified from Gradle tasks)
- Run client: `./gradlew runClient`
- Run dedicated server: `./gradlew runServer`
- Regenerate advancements/datagen output: `./gradlew runDatagen` (writes to `src/main/generated`, included as resources in `build.gradle`).
- Build jars: `./gradlew build` (includes remapped mod jar + sources jar via Loom setup).
- Validate AW rules after touching access widener/mixins: `./gradlew validateAccessWidener`.
- Useful for MC source lookup while changing mixins: `./gradlew genSources`.

## Project-specific conventions to preserve
- Config gate pattern is mandatory for gameplay logic: check `FTConfig.isTrimEnabled("<material>")` before applying effects.
- Persisted config filename is `functionaltrims.json` (`ConfigManager`); new tunables usually require updates in all of:
  - `FunctionalTrimsConfig` (data model),
  - `FunctionalTrimsConfigScreen` (UI field),
  - usage sites (effect/mixin/event code).
- Advancement triggering is event-driven using `ModCriteria.TRIM_TRIGGER.trigger(player, material, action)`; when adding new actions, wire them in `TrimAdvancementProvider` and lang keys.
- Redstone effect is split across ticker + mixin (`RedstoneTrimPowerTicker` + `RedstoneViewMixin`); changing one without the other causes desync or no signal updates.
- Several mixins cache config values in `static final` fields (for example `IronTrimEffect`, `LootTableMixin`), which means runtime config edits may not refresh until restart.

## Integration points and dependencies
- Loader/API stack: Fabric Loader + Fabric API (`build.gradle`, `fabric.mod.json`).
- Optional UI integration: Mod Menu entrypoint `functional_trims.config.FunctionalTrimsModMenuIntegration`.
- Config UI dependency: Cloth Config (`me.shedaniel.cloth`).
- Mixin config: `src/main/resources/functional_trims.mixins.json`; keep new mixins registered here.
- Access widener: `src/main/resources/functional_trims.accesswidener`; validate after edits.

## When adding a new trim effect
- Mirror existing pattern from `CopperTrimEffect` or `ResinTrimEffect`: implement behavior, register in `FunctionalTrims`, gate via `FTConfig`, and trigger criteria events.
- Add config section + screen controls + `FTConfig.isTrimEnabled` case.
- Add advancement nodes in `TrimAdvancementProvider`, then run datagen and review JSON changes under `src/main/generated/data/functional_trims/advancement/`.
- Add/extend localization entries in `src/main/resources/assets/functional_trims/lang/en_us.json` (and other locales if maintained).

## minecraft-dev MCP server (AI tooling)

The `minecraft-dev` MCP server provides tools for looking up live Minecraft source, validating mixins and access wideners, and comparing versions — all without running `./gradlew genSources`. The current target version is **`26.1`** (already cached locally). Because 26.x is fully unobfuscated (see [versioning section above](#minecraft-versioning-and-mapping-system)), always pass `mapping=mojmap`; yarn is not applicable.

### Available tools and what they do

| Tool                               | Purpose                                                                                             | Verified for this project                                                                                  |
|------------------------------------|-----------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|
| `list_minecraft_versions`          | Lists all cached and downloadable MC versions                                                       | ✅ `26.1` already cached                                                                                    |
| `get_minecraft_source`             | Returns full decompiled source for a fully-qualified class                                          | ✅ Used to read `ArmorTrim`, `TrimMaterials`                                                                |
| `search_minecraft_code`            | Regex/literal search over decompiled source by `class`, `method`, `field`, or `content`             | ✅ Found `DATA_SHARED_FLAGS_ID` in `Entity`, all trim classes                                               |
| `find_mapping`                     | Translates symbol names between official/intermediary/yarn/mojmap                                   | ⚠️ N/A for 26.1 — already unobfuscated                                                                     |
| `validate_access_widener`          | Parses and validates an `.accesswidener` file against MC source                                     | ✅ Caught namespace mismatch in project AW (see note below)                                                 |
| `analyze_mixin`                    | Parses `@Mixin` annotations and validates injection targets                                         | ✅ Confirmed `LivingEntityMixin` valid; ⚠️ interface-based `@Mixin` (e.g. `RedstoneViewMixin`) not detected |
| `get_registry_data`                | Runs the MC data generator to dump registry entries                                                 | ❌ Fails on 26.1 — requires Java 25 (class file 69), runtime only has Java 22                               |
| `get_documentation`                | Looks up Fabric/MC wiki docs by class name                                                          | ⚠️ Sparse — returned nothing for `ArmorTrim` or `MobEffect`                                                |
| `search_documentation`             | Full-text search across documentation topics                                                        | ⚠️ Sparse — returned no results for tested queries                                                         |
| `decompile_minecraft_version`      | Downloads and decompiles an entire MC version (needed before `validate_access_widener` with `yarn`) | Not yet run for 26.1                                                                                       |
| `index_minecraft_version`          | Builds a full-text FTS5 index over decompiled source                                                | Not yet run for 26.1                                                                                       |
| `search_indexed`                   | Fast FTS5 search (AND/OR/NOT/"phrase"/prefix) — requires `index_minecraft_version` first            | Not yet run                                                                                                |
| `compare_versions`                 | High-level diff of classes and registry data between two versions                                   | Not tested                                                                                                 |
| `compare_versions_detailed`        | AST-level diff: method signatures, fields, breaking changes per package                             | Not tested                                                                                                 |
| `analyze_mod_jar`                  | Extracts metadata, entry points, mixins from a third-party mod JAR                                  | Not tested                                                                                                 |
| `remap_mod_jar`                    | Remaps an intermediary mod JAR to yarn or mojmap names                                              | Not tested                                                                                                 |
| `decompile_mod_jar`                | Decompiles a mod JAR (original or remapped) to readable Java                                        | Not tested                                                                                                 |
| `search_mod_code`                  | Regex/literal search over a decompiled mod's source                                                 | Not tested                                                                                                 |
| `index_mod` / `search_mod_indexed` | FTS5 index + search for a decompiled mod                                                            | Not tested                                                                                                 |

### Key findings from testing

- **Trim materials in 26.1** (`TrimMaterials`): quartz, iron, netherite, redstone, copper, gold, emerald, diamond, lapis, amethyst, resin — 11 total. Each maps to a `ResourceKey<TrimMaterial>` under `net.minecraft.world.item.equipment.trim`.
- **`DATA_SHARED_FLAGS_ID`** (used by `EntityAccessor` / `AmethystVisionEffect`) is `protected static final EntityDataAccessor<Byte>` on `Entity` (line 260 of `Entity.java`). The glow bit is `0x40` (bit 6), consistent with current code.
- **`ArmorTrim`** is now a `record` in `net.minecraft.world.item.equipment.trim` with `material()` and `pattern()` accessors.
- **`SignalGetter`** (target of `RedstoneViewMixin`) lives in `net.minecraft.world.level.SignalGetter`; `hasNeighborSignal` and `getDirectSignalTo` are confirmed present.
- **Access widener issue**: `functional_trims.accesswidener` declares namespace `official` but uses Yarn-style class descriptors (`ServerPlayerEntity`, `AbstractCriterion`). The validator flags this. When migrating to 26.1 mojmap names the AW should reference `net/minecraft/server/level/ServerPlayer` and `net/minecraft/advancements/critereon/AbstractCriterion`.

### Recommended workflow when touching mixins or the AW
1. Use `get_minecraft_source` or `search_minecraft_code` to confirm the target class and method descriptor exist in the current version before writing/editing a mixin.
2. Run `analyze_mixin` on the edited file to catch injection target mismatches early (works on class-based mixins; interface mixins need manual review).
3. Run `validate_access_widener` to check AW entries after any edit (pass the file path and `mapping=mojmap` for 26.1).
4. For broad lookups across the whole codebase first run `index_minecraft_version` (one-time), then use `search_indexed` for fast FTS5 queries.
