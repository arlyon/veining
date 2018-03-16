# Contributing

If you have any improvements in mind or bug fixes up your sleeve, PRs are welcome. Localization is always appreciated.
Translations are crude and google-translated so if you have a better translation please open an issue or correct it
yourself.

## Setting up the Dev Environment

To set up the development environment in IDEA:

- clone the repo
- run `./gradlew setupDecompWorkspace`
- run `./gradlew genIntellijRuns`
- ???
- profit

## Update Checklist

- build and test game
- bump version number in `Veining.class` and in `mcmod.info`
- bump version number in `build.properties`
- add changes to changelog
- write to update.json
- commit and add tag
- push tags to github `git push --tags`
- push to github `./gradlew githubRelease`
- push to curseforge `./gradlew curseforge`