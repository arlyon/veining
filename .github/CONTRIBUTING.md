# Contributing

## Setting up the Dev Environment

If you have any improvements in mind or bug fixes up your sleeve, PRs are welcome. Localization is always appreciated.
Translations are crude and google-translated so if you have a better translation please open an issue or correct it
yourself.

To set up the development environment:

- clone the repo
- run `./gradlew setupDecompWorkspace`
- run `./gradlew genIntellijRuns`
- ???
- profit

## Update Checklist

- bump version number in `Veining.class` and in `mcmod.info`
- bump version number in `build.properties`
- add changes to changelog
- build and test game
- commit and add tag
- push to curseforge