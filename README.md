# Create JEI Compat

A compatibility mod for Create and JEI that enhances the display of sequenced assembly recipes in JEI.

## License

This mod is licensed under the MIT License. See [LICENSE](LICENSE) file for details.

## Features

- **Pagination Support**: Displays sequenced assembly recipes with pagination (6 steps per page)
- **Scroll Navigation**: Use mouse scroll wheel to navigate between pages
- **Page Indicator**: Shows current page number (e.g., "1/2") in the bottom-right corner
- **Automatic Slot Updates**: Recipe slots automatically update when scrolling between pages
- **Supports up to 18 steps**: Uses Roman numerals (I-XVIII) to label recipe steps

## Installation

1. Install Minecraft 1.21.1
2. Install NeoForge
3. Install Create mod (version 6.0.0 or above)
4. Install JEI
5. Place this mod in your mods folder

## Usage

When viewing a sequenced assembly recipe in JEI that has more than 6 steps:
- Scroll up/down with your mouse wheel to navigate between pages
- The page indicator shows your current position (e.g., "1/2", "2/2")
- Recipe slots automatically update to show ingredients for the current page

## Building

Run `./gradlew build` to build the mod. The output JAR will be in `build/libs/`.