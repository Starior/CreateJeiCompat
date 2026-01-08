# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

