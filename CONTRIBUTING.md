# Contributing to Functional Trims

Contributions are welcome — bug fixes, translations, balance tweaks, and new trim ideas.

## Supported versions

Only the **latest supported Minecraft version** is actively developed, on the `main` branch.

| Branch | Minecraft | Status |
|---|---|---|
| `main` | 26.2 | Active development |
| `legacy/26.1` | 26.1.x | Frozen — community backports welcome |
| `legacy/1.21.11` | 1.21.11 | Frozen — community backports welcome |
| `legacy/1.21.9` | 1.21.9 | Frozen — community backports welcome |
| `legacy/1.21.8` | 1.21.8 | Frozen — community backports welcome |
| `legacy/1.21.1` | 1.21.1 | Frozen — community backports welcome |

New features and fixes land on `main` only. If you want a change on an older version,
open a PR against the matching `legacy/*` branch — CI builds those branches too.

## Pull requests

- Base PRs on `main` (or a `legacy/*` branch for backports).
- Keep changes focused; avoid unrelated refactors.
- For gameplay changes, include a short note on the design intent.
- If you change advancements or other datagen output, run `./gradlew runDatagen`
  and commit the changes under `src/main/generated/`.

## Translations

1. Copy `src/main/resources/assets/functional_trims/lang/en_us.json`
2. Rename it to your language code (e.g. `de_de.json`)
3. Translate the values, keeping the keys unchanged
4. Open a pull request

## Development setup

1. Clone the repository and open it in IntelliJ IDEA (JDK 25).
2. Run the game with `./gradlew runClient` (or `runServer`).
3. Build jars with `./gradlew build` — output goes to `build/libs/`.

## Versioning

- `mod_version` in `gradle.properties` is the only version you edit.
- Jars are named `functional_trims-<mod_version>+<minecraft_version>.jar`.
- Releases are tagged `v<mod_version>+<minecraft_version>`, e.g. `v2.2.1+26.2`.

## Releasing (maintainers)

1. Bump `mod_version` in `gradle.properties` and add a `## <mod_version>` section to `CHANGELOG.md`.
2. Merge to `main` and wait for CI to pass.
3. Tag and push: `git tag v<mod_version>+<minecraft_version> && git push origin --tags`
   (e.g. `v2.3.0+26.2`). The `publish` workflow uploads the jar to Modrinth,
   CurseForge and GitHub Releases, tagged with `publish_minecraft_versions`.

To check what would be published without uploading anything, run the `publish`
workflow manually from the Actions tab and download the `publish-dry-run` artifact.
