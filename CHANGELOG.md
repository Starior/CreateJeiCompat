# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

**Note:** This changelog is for the Forge 1.20.1 branch. For NeoForge 1.21.1, see the `master` branch.

## [Unreleased]

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

