# AGENTS.md

## What this project is
- `functional_trims` is a mod that turns full armor trim sets into gameplay effects, built for **both Fabric and NeoForge** from one shared codebase.
- `common/src` holds almost all gameplay code (config, criteria, effects, events, mixins) and is loader-neutral; it is not a Gradle subproject, it's compiled as an extra source directory by both `fabric/` and `neoforge/` (see "Multi-loader layout" below for why).
- Entrypoints are thin: `common/.../FunctionalTrimsCommon.init()` does the real bootstrap (register effects/criteria/events -> tick handlers evaluate full-set trim state and apply behavior); `fabric/.../FunctionalTrimsFabric` and `neoforge/.../FunctionalTrimsNeoForge` just call into it.
- Shared trim check logic lives in `common/src/main/java/rubbertoe/functional_trims/func/TrimHelper.java` (`countTrim` / `hasFullTrim`), and most gameplay code assumes `count == 4` for activation.

## Multi-loader layout (Fabric + NeoForge via Forgified Fabric API)
- The NeoForge build depends on **Forgified Fabric API (FFAPI)** instead of hand-porting every Fabric API hook. FFAPI reimplements `net.fabricmc.fabric.api.*` on top of NeoForge, so almost all of `common`'s code (which only uses `ServerTickEvents`, `AttackEntityCallback`, `ServerLivingEntityEvents`, `ServerPlayConnectionEvents`) runs unchanged on both loaders. Cloth Config's `me.shedaniel.clothconfig2.api.*` package is likewise identical across its `cloth-config-fabric`/`cloth-config-neoforge` artifacts, so `FunctionalTrimsConfigScreen` is shared too.
- **`common/` is not a Gradle subproject.** Fabric Loom (1.17.0-alpha.19) cannot be applied to more than one project in the same build without corrupting its shared extension state (`ClassCastException` on `LoomGradleExtensionImpl_Decorated`, reproduced empirically) — so instead of a compiled `common` artifact, `fabric/build.gradle` and `neoforge/build.gradle` each add `common/src/main/java` and `common/src/main/resources` as extra `sourceSets.main` directories and compile it twice. Keep this in mind: a change under `common/` affects both loaders automatically, but there is no separately-built `common.jar` to depend on.
- Only two things in `common` are genuinely loader-specific, both handled via a tiny `ServiceLoader`-based `rubbertoe.functional_trims.platform.Platform` interface (config dir path) plus loader-specific entrypoint/config-screen glue in `fabric/` and `neoforge/`:
  1. Config directory path (`ConfigManager` uses `Services.PLATFORM.getConfigDir()`).
  2. Mod entrypoint + config-screen registration (ModMenu entrypoint on Fabric; a `RegisterEvent`-based NeoForge `IConfigScreenFactory` on NeoForge).
- **`BuiltInRegistries.TRIGGER_TYPES` registration is platform-sensitive**: Fabric tolerates registering into it eagerly during `ModInitializer.onInitialize()` (`ModCriteria.register()`, called from `FunctionalTrimsFabric`), but NeoForge freezes that registry *before* mod construction runs. NeoForge must register via a `RegisterEvent` listener on the mod event bus instead (see `FunctionalTrimsNeoForge`). If you add another built-in-registry entry outside the moddable registries (blocks/items/etc.), expect the same split.
- Datagen (`FunctionalTrimsDataGenerator`, `TrimAdvancementProvider`) is Fabric-only dev tooling — it's not shipped at runtime, so it lives in `fabric/`, but its output (`common/src/main/generated/`) is shared by both loaders' resources.

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

This project targets **`26.2`** (set in `gradle.properties` → `minecraft_version=26.2`).

### Mojang now ships unobfuscated code — yarn is obsolete
Starting with this version era, **Mojang publishes fully human-readable ("mojmap") names in the production JAR** — no more obfuscated `a`, `b`, `c` class/field/method names. Practical consequences:

- **Yarn mappings are no longer needed or maintained** for these versions. Do not add or reference yarn dependencies.
- **All source lookups, mixins, and access-widener entries use mojmap names** (the same names Mojang chose, e.g. `net.minecraft.world.entity.Entity`, `DATA_SHARED_FLAGS_ID`).
- **`find_mapping`** (the MCP symbol translator) is a no-op for 26.x — passing `sourceMapping=mojmap` will error because there is no translation layer.
- Fabric Loom still uses an `intermediary` layer internally for stable remapping, but you never write or read intermediary names directly when working on this mod.
- The project currently has no access widener. If one is needed, create it with mojmap names (e.g. `net/minecraft/server/level/ServerPlayer`), register it in `fabric.mod.json`, and validate it.

## Branches and versions
- `main` is the only actively developed branch and always targets the latest supported Minecraft version.
- `legacy/<mc-version>` branches are frozen snapshots of older versions (pre-dating the Fabric+NeoForge split — they are still Fabric-only single-module builds). Do not port features to them; community backports are accepted as PRs against them.
- Jar versions are `<mod_version>+<minecraft_version>` (set per-module in `fabric/build.gradle` / `neoforge/build.gradle`); bump `mod_version` in the root `gradle.properties` only. Jar file names are `functional_trims-fabric-<version>.jar` / `functional_trims-neoforge-<version>.jar`.
- Java package root is `rubbertoe.functional_trims`; the mod ID / resource namespace is `functional_trims` on both loaders.

## Architecture map (read these first)
- `common/src/main/java/rubbertoe/functional_trims/FunctionalTrimsCommon.java`: bootstrap and registration order, called from both loader entrypoints.
- `common/src/main/java/rubbertoe/functional_trims/platform/`: the `Platform` interface + `ServiceLoader` lookup (`Services`) that's the only generic loader abstraction; see "Multi-loader layout" above.
- `common/src/main/java/rubbertoe/functional_trims/config/`: JSON config model + Cloth Config screen (shared). `FunctionalTrimsModMenuIntegration` (Fabric-only) lives in `fabric/`; `FunctionalTrimsNeoForgeConfigIntegration` (NeoForge-only) lives in `neoforge/`.
- `common/src/main/java/rubbertoe/functional_trims/trim_effect/`: standalone trim behaviors (often event/tick registration per effect).
- `common/src/main/java/rubbertoe/functional_trims/event/`: global listeners and tickers (advancement grants, charged attacks, redstone powering).
- `common/src/main/java/rubbertoe/functional_trims/mixin/`: vanilla behavior patches for effects that cannot be done through API events. Pure vanilla, zero Fabric imports, shared unchanged by both loaders via one `functional_trims.mixins.json`.
- `common/src/main/java/rubbertoe/functional_trims/criteria/`: custom advancement trigger plumbing. `fabric/src/main/java/rubbertoe/functional_trims/datagen/TrimAdvancementProvider.java` (Fabric-only dev tool) generates the advancement graph into `common/src/main/generated/`.
- `fabric/src/main/java/rubbertoe/functional_trims/FunctionalTrimsFabric.java` / `neoforge/src/main/java/rubbertoe/functional_trims/FunctionalTrimsNeoForge.java`: the actual `@ModInitializer`/`@Mod` entrypoints.

## Build and dev workflows (verified from Gradle tasks)
- Run Fabric client: `./gradlew :fabric:runClient` / server: `./gradlew :fabric:runServer`
- Run NeoForge client: `./gradlew :neoforge:runClient` / server: `./gradlew :neoforge:runServer`
- Regenerate advancements/datagen output: `./gradlew :fabric:runDatagen` (writes to `common/src/main/generated`, included as a resource dir by both loader modules). **This folder is committed** — always commit regenerated output; CI fails without it.
- Build both jars: `./gradlew build` (builds `:fabric:build` and `:neoforge:build`; output in `fabric/build/libs/` and `neoforge/build/libs/`).
- Useful for MC source lookup while changing mixins (Fabric side): `./gradlew :fabric:genSources`. NeoForge decompiles its own Minecraft sources automatically into its Gradle cache the first time any `:neoforge:*` task runs (look for `neoforge-*-sources.jar` / `minecraft-patched-*-sources.jar` under `~/.gradle/caches/modules-2` and `neoforge/build/moddev/artifacts`) — there's no separate `genSources` task to run for it.

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
- Fabric stack: Fabric Loader + Fabric API (`fabric/build.gradle`, `fabric/src/main/resources/fabric.mod.json`).
- NeoForge stack: NeoForge + Forgified Fabric API (`neoforge/build.gradle`, `neoforge/src/main/resources/META-INF/neoforge.mods.toml`).
- Optional Fabric UI integration: Mod Menu entrypoint `rubbertoe.functional_trims.config.FunctionalTrimsModMenuIntegration`.
- Config UI dependency: Cloth Config (`me.shedaniel.cloth`).
- Mixin config: `common/src/main/resources/functional_trims.mixins.json`; keep new mixins registered here.
- **Dedicated-server safety:** `common/` and anything the NeoForge `@Mod` constructor loads unconditionally must not touch client-only classes (e.g. `Screen`); the NeoForge config-screen hookup is guarded by `FMLEnvironment.getDist() == Dist.CLIENT`. Smoke-test with `./gradlew :neoforge:runServer` after touching entrypoints.

## When adding a new trim effect
- Mirror existing pattern from `CopperTrimEffect` or `ResinTrimEffect`: implement behavior, register in `FunctionalTrimsCommon`, gate via `FTConfig`, and trigger criteria events.
- Add config section + screen controls + `FTConfig.isTrimEnabled` case.
- Add advancement nodes in `TrimAdvancementProvider`, then run datagen and review JSON changes under `common/src/main/generated/data/functional_trims/advancement/`.
- Add/extend localization entries in `common/src/main/resources/assets/functional_trims/lang/en_us.json` (and other locales if maintained).

## minecraft-dev MCP server (AI tooling)

The `minecraft-dev` MCP server provides tools for looking up live Minecraft source, validating mixins and access wideners, and comparing versions — all without running `./gradlew genSources`. The current target version is **`26.2`**. Because 26.x is fully unobfuscated (see [versioning section above](#minecraft-versioning-and-mapping-system)), always pass `mapping=mojmap`; yarn is not applicable.

### Available tools and what they do

| Tool                               | Purpose                                                                                             | Verified for this project                                                                                  |
|------------------------------------|-----------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|
| `list_minecraft_versions`          | Lists all cached and downloadable MC versions                                                       | ✅ `26.1` already cached                                                                                    |
| `get_minecraft_source`             | Returns full decompiled source for a fully-qualified class                                          | ✅ Used to read `ArmorTrim`, `TrimMaterials`                                                                |
| `search_minecraft_code`            | Regex/literal search over decompiled source by `class`, `method`, `field`, or `content`             | ✅ Found `DATA_SHARED_FLAGS_ID` in `Entity`, all trim classes                                               |
| `find_mapping`                     | Translates symbol names between official/intermediary/yarn/mojmap                                   | ⚠️ N/A for 26.1 — already unobfuscated                                                                     |
| `validate_access_widener`          | Parses and validates an `.accesswidener` file against MC source                                     | ✅ Works with `mapping=mojmap` (project currently has no AW)                                                |
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

### Key findings from testing (recorded on 26.1; re-check on newer versions)

- **Trim materials in 26.1** (`TrimMaterials`): quartz, iron, netherite, redstone, copper, gold, emerald, diamond, lapis, amethyst, resin — 11 total. Each maps to a `ResourceKey<TrimMaterial>` under `net.minecraft.world.item.equipment.trim`.
- **`DATA_SHARED_FLAGS_ID`** (used by `EntityAccessor` / `AmethystVisionEffect`) is `protected static final EntityDataAccessor<Byte>` on `Entity` (line 260 of `Entity.java`). The glow bit is `0x40` (bit 6), consistent with current code.
- **`ArmorTrim`** is now a `record` in `net.minecraft.world.item.equipment.trim` with `material()` and `pattern()` accessors.
- **`SignalGetter`** (target of `RedstoneViewMixin`) lives in `net.minecraft.world.level.SignalGetter`; `hasNeighborSignal` and `getDirectSignalTo` are confirmed present.

### Recommended workflow when touching mixins or the AW
1. Use `get_minecraft_source` or `search_minecraft_code` to confirm the target class and method descriptor exist in the current version before writing/editing a mixin.
2. Run `analyze_mixin` on the edited file to catch injection target mismatches early (works on class-based mixins; interface mixins need manual review).
3. If an access widener is added, run `validate_access_widener` on it (pass `mapping=mojmap`).
4. For broad lookups across the whole codebase first run `index_minecraft_version` (one-time), then use `search_indexed` for fast FTS5 queries.
