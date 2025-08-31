
# Ktchanges

Kotlin equivalent of Javascript's changesets CLI


## Installation/Usage

To install, add the following line to the plugin section of your `build.gradle.kts` file.

```kt
plugin("nl.klrnbk.daan.ktchanges") version "1.0.0"
```


## Running Tests

To run tests, run the following command (make sure you are located in the `./plugins` directory)

```bash
./gradlew test
```


## Usage

You can use ktchanges by running `./gradlew ktchanges`. Make sure that git is initialized and you configured your `.ktchanges/config.yaml` settings correctly.

Example of a `.ktchanges/config.yaml` config file:

```yaml
enabled: true
baseBranch: origin/main
sources:
  - test-project/src
```

The CLI will prompt you to select projects within the selected sources (for this example `test-project/src`) and ask you whether to perform a _major_, _minor_ or _patch_ bump.

It will then create a changes file in the `.ktchanges` directory that can be read by the action included in this repository (TO BE CREATED).
