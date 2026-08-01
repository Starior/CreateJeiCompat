# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

**Note:** This changelog is for the Forge 1.20.1 branch. For NeoForge 1.21.1, see the `master` branch.

## [1.0.2] - 2026-08-01

### Fixed
- Removed the `mixinextras` entry from `mods.toml`. Users reported that deleting those lines made 1.0.1 load on Forge 1.20.1; Forge does not treat MixinExtras as a normal mod id, so the required-dependency check fails. We do not fully understand why Forge resolves it that way — dropping the declaration matches what works in practice. MixinExtras usage in code is unchanged.

## [1.0.1] - 2026-07-27

### Added
- Page-turn buttons and Up/Down/Left/Right keys via `handleInput` (works in JEI and EMI/JEmi)
- Optional EMI soft dependency (JEI remains required; EMI shows the same category via JEmi)
- Page controls work in EMI when JEI+EMI are both installed
- Invisible ingredients for off-page steps so recipe search still finds them
- In-place JEI/EMI layout refresh after page changes
- Client config (`showPageArrows`, default off) via Create/Ponder `BaseConfigScreen`; toggles clickable `< >` only — `1/N` always shows; no game restart needed after toggling

### Changed
- Page controls are a horizontal row (`< 1/N >` when arrows enabled, else `1/N`); darker enabled/page text, slightly lighter on hover; disabled translucent
- Mouse wheel over any sequenced recipe in EMI blocks EMI recipe-page scroll; multi-page recipes also turn step pages

### Fixed
- Cyber Goggles scrap/byproduct row under sequenced assembly only when Cyber Goggles is present (WrapMethod skips CCG's `setRecipe` TAIL; we re-add scrap solely in that case)
- Scrap laid out in rows of up to 9 (fits Create's 180px JEI width), each row centered (y=114); CCG's two-row `+40` height pad trimmed to one row when Cyber Goggles is present
- Create JEI `emptyBackground` height +1 (real drawable) so page controls clear the row above

### Notes
- Thanks to [15aigoa](https://github.com/15aigoa/CreateJeiCompat_EmiFix) for the EMI-oriented fork that explored page controls, invisible off-page ingredients, and JEI/EMI layout refresh while an issue was waiting.

## [1.0.1-beta] - 2026-07-26

### Fixed
- Replace `@Overwrite` on sequenced-assembly JEI methods with MixinExtras `@WrapMethod` so Create: Cyber Goggles can still apply its `setRecipe*` TAIL inject (scrap outputs) alongside pagination

## [1.0.0] - 2025-02-28

### Added
- Pagination support for Sequenced Assembly recipes (6 steps per page)
- Mouse scroll wheel navigation between pages
- Page indicator in bottom-right corner showing current page (e.g., "1/2")
- Automatic recipe slot updates when scrolling between pages
- Roman numerals to label recipe steps (supports up to 3999 steps)
- Prevents JEI page scrolling when cursor is over recipe at page boundaries
