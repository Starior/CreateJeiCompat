# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

**Note:** This changelog is for the NeoForge 1.21.1 (`master`) branch. For Forge 1.20.1, see the `forge-1.20.1` branch.

## [1.0.3] - 2026-07-27

NeoForge port of Forge 1.0.1 feature set.

### Added
- Page-turn buttons and Up/Down/Left/Right keys via `handleInput` (works in JEI and EMI/JEmi)
- Optional EMI soft dependency (JEI remains required; EMI shows the same category via JEmi)
- Page controls work in EMI when JEI+EMI are both installed
- Invisible ingredients for off-page steps so recipe search still finds them
- In-place JEI/EMI layout refresh after page changes
- Client config (`showPageArrows`, default off) via Create/Ponder `BaseConfigScreen`; toggles clickable `< >` only — `1/N` always shows; no game restart needed after toggling

### Changed
- Replaced `@Overwrite` on sequenced-assembly JEI methods with MixinExtras `@WrapMethod` so other mods can still target `setRecipe`
- Page controls are a horizontal row (`< 1/N >` when arrows enabled, else `1/N`); darker enabled/page text, slightly lighter on hover; disabled translucent
- Mouse wheel over any sequenced recipe in EMI blocks EMI recipe-page scroll; multi-page recipes also turn step pages

### Fixed
- Create JEI `emptyBackground` height +1 (real drawable) so page controls clear the row above
- EMI step-page scroll with JEI 19 `RecipeHolder` recipes (JEmi); wheel no longer flips EMI recipe pages instead

### Notes
- Thanks to [15aigoa](https://github.com/15aigoa/CreateJeiCompat_EmiFix) for the EMI-oriented fork that explored page controls, invisible off-page ingredients, and JEI/EMI layout refresh while an issue was waiting.

## [1.0.2] - 2024-12-XX

### Fixed
- Fixed version not being updated in neoforge.mods.toml (now uses dynamic version from git tags)

## [1.0.1] - 2024-12-XX

### Changed
- Replaced static Roman numeral array with dynamic calculation function (supports up to 3999 steps)
- Prevents JEI page scrolling when cursor is over recipe at page boundaries

## [1.0.0] - 2024-12-XX

### Added
- Pagination support for Sequenced Assembly recipes (6 steps per page)
- Mouse scroll wheel navigation between pages
- Page indicator in bottom-right corner showing current page (e.g., "1/2")
- Automatic recipe slot updates when scrolling between pages
- Roman numerals to label recipe steps
