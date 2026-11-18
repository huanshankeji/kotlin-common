# Repository Guidelines

## Project Structure

`core/` contains Kotlin and standard-library extensions. `net/` and `web/` hold shared networking and web utilities. Integrations live in their own modules, such as `ktor/client/`, `serialization/`, `vertx/`, and `testcontainers/`. Serialization benchmarks and Protobuf schemas live under `serialization/benchmark/`.

Put portable Kotlin code in `src/commonMain/kotlin` and platform-specific code in source sets such as `src/jvmMain/kotlin`. JVM-only modules use `src/main/kotlin`. `settings.gradle.kts` defines the modules, `buildSrc/` holds shared Gradle conventions, and module `api/` directories contain tracked ABI snapshots.

## Build and Development

Use the Gradle wrapper from the repository root. Run Gradle with JDK 17 and make JDK 11 available for compilation. CI runs on macOS, which supports the iOS targets. See [CONTRIBUTING.md](CONTRIBUTING.md) for GitHub Packages credentials when resolving `*-dev-commit-*` dependencies.

- `./gradlew build` builds and verifies the libraries.
- `./gradlew check` runs verification, including tests and ABI checks where configured.
- `./gradlew publishToMavenLocal` publishes artifacts for testing in downstream projects.
- `./gradlew :dokkaGeneratePublicationHtml` generates API documentation.

## Kotlin Style and Dependencies

Use four spaces, match package directories under `com.huanshankeji`, and use `UpperCamelCase` for types and `lowerCamelCase` for functions and properties. Follow the [organization’s Kotlin style guide](https://github.com/huanshankeji/.github/blob/main/docs/kotlin-code-style.md) when formatting in IntelliJ IDEA.

Declare upstream libraries and BOMs with `implementation` or the matching feature’s `Implementation` configuration, even when public signatures use upstream types. Consumers declare those dependencies explicitly. See [README.md](README.md#declare-upstream-dependencies-explicitly).

## Testing

Name test classes `*Test` and give test methods descriptive names, such as `testConversionBetweenLongAndBigEndianBytes`. Use `kotlin.test` for assertions and Kotest for property tests in `core`. Cover shared behavior in `src/commonTest/kotlin` and platform behavior in the corresponding target test source set. JVM-only modules use `src/test/kotlin`.

- `./gradlew :kotlin-common-core:jvmTest` runs the JVM suite. Use `jsBrowserTest` or `wasmJsBrowserTest` for the browser targets.
- `./gradlew :kotlin-common-vertx:test` runs the JVM-only Vert.x suite.

Review changes to the public API before regenerating the snapshots with [`./gradlew updateKotlinAbi`](https://kotlinlang.org/docs/gradle-binary-compatibility-validation.html#update-reference-abi-dump).

## Commits and Pull Requests

Use short, imperative commit subjects, such as `Require consumers to declare Testcontainers dependencies`. In pull requests, explain the change and its motivation, link related issues or discussions when available, and report the checks you ran. Flag intentional API changes so reviewers can inspect the ABI diff.
