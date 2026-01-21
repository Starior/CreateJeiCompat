# Create JEI Compat

A compatibility mod for Create and JEI that enhances the display of sequenced assembly recipes in JEI.

This mod extends the functionality of [Create mod](https://github.com/Creators-of-Create/Create) and [JEI (Just Enough Items)](https://github.com/mezz/JustEnoughItems) by adding pagination support to sequenced assembly recipe displays in JEI. The implementation uses Mixin to enhance Create mod's `SequencedAssemblyCategory` class with pagination features.

## License

This mod is licensed under the MIT License. See [LICENSE](LICENSE) file for details.

## Features

- **Pagination Support**: Displays sequenced assembly recipes with pagination (6 steps per page)
- **Scroll Navigation**: Use mouse scroll wheel to navigate between pages
- **Page Indicator**: Shows current page number (e.g., "1/2") in the bottom-right corner
- **Automatic Slot Updates**: Recipe slots automatically update when scrolling between pages
- **Unlimited Steps Support**: Uses dynamic Roman numeral conversion (I, II, III... up to MMMCMXCIX) to label recipe steps
- **Smart Scroll Handling**: Prevents JEI page scrolling when cursor is over recipe at page boundaries

## Usage

When viewing a sequenced assembly recipe in JEI that has more than 6 steps:
- Scroll up/down with your mouse wheel to navigate between pages
- The page indicator shows your current position (e.g., "1/2", "2/2")
- Recipe slots automatically update to show ingredients for the current page

## Building

Run `./gradlew build` to build the mod. The output JAR will be in `build/libs/`.