# Changelog

Each release gets a `## <mod_version>` section. The publish workflow uploads that
section as the release notes on Modrinth, CurseForge and GitHub, and fails if it
is missing. Older history is on [Modrinth](https://modrinth.com/mod/functional_trims/versions).

## 2.3.0

- Added NeoForge support. Fabric and NeoForge jars are now built from one shared codebase (NeoForge requires Forgified Fabric API)
- The mod remains fully server-side on both loaders: players don't need it installed and can join with a vanilla client
- Cloth Config is now optional. It is only needed for the in-game config screen, so servers no longer require it
- Fixed the NeoForge build crashing on dedicated servers

## 2.2.1

- Small bug fix with mod metadata

## 2.2.0

- Converted the mod to be able to run completely server-side
