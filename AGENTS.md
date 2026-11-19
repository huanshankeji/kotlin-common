# Repository Guidelines

Before working in this repository, **load and read** the [Huanshankeji general agent instructions](https://github.com/huanshankeji/.github/blob/main/docs/general-agent-instructions.md) as the required organization-wide baseline. A skipped or failed load, or an unread document, is a blocking error until the document is in context and has been read.

## Project Structure

`core/` contains Kotlin and standard-library extensions. `net/` and `web/` hold shared networking and web utilities. Integrations live in their own modules, such as `ktor/client/`, `serialization/`, `vertx/`, and `testcontainers/`. Serialization benchmarks and Protobuf schemas live under `serialization/benchmark/`.

Put portable Kotlin code in `src/commonMain/kotlin` and platform-specific code in source sets such as `src/jvmMain/kotlin`. JVM-only modules use `src/main/kotlin`. `settings.gradle.kts` defines the modules, `buildSrc/` holds shared Gradle conventions, and module `api/` directories contain tracked ABI snapshots.

## Build and Development

Use the Gradle wrapper from the repository root. Run Gradle with JDK 17 and make JDK 11 available for compilation. [CI](.github/workflows/ci.yml) runs on macOS, which supports the iOS targets. For unpublished sibling library changes, follow the [organization’s local development workflow](https://github.com/huanshankeji/.github/blob/main/docs/dev-instructions.md#local-development-workflow). See [CONTRIBUTING.md](CONTRIBUTING.md) for GitHub Packages credentials when resolving `*-dev-commit-*` dependencies.

- `./gradlew build` builds and verifies the libraries.
- `./gradlew check` runs verification, including tests and ABI checks where configured.
- Run `./gradlew checkKotlinAbi` to compare the current ABI with the tracked reference snapshots. For intentional ABI changes, run `./gradlew updateKotlinAbi` and review the generated diff before including the updated snapshots. See [Kotlin’s ABI validation guide](https://kotlinlang.org/docs/gradle-binary-compatibility-validation.html).
- `./gradlew publishToMavenLocal` publishes artifacts for testing in downstream projects.
- `./gradlew :dokkaGeneratePublicationHtml` generates API documentation.

## Kotlin Style

Use four spaces, match package directories under `com.huanshankeji`, and use `UpperCamelCase` for types and `lowerCamelCase` for functions and properties. Follow the [organization’s Kotlin style guide](https://github.com/huanshankeji/.github/blob/main/docs/kotlin-code-style.md) when formatting in IntelliJ IDEA.

## Dependency Policy

This project provides **extension libraries**. Consumers are expected to explicitly declare the original upstream libraries they use, alongside our extension modules.

- Declare upstream library dependencies with `implementation`, including when upstream types appear in public extension receivers, parameters, return types, or supertypes. The upstream type alone does not justify exposing the dependency through `api`.
- Apply the same rule to upstream BOMs and feature variants. Use `implementation(platform(...))` and the feature's `Implementation` configuration, such as `postgresqlImplementation` or `vertxWebImplementation`.
- Dependencies between this project's own modules may use `api` when intentionally re-exporting our own public types. For example, `testcontainers` exposes `:net` because `hostAndPort()` returns `HostAndPort`.

`implementation` keeps upstream dependencies off consumers' compile classpaths. On the JVM they still participate in runtime dependency resolution. Consumers declare the upstream modules and any BOMs needed to compile their code. See [README.md](README.md#declare-upstream-dependencies-explicitly) for examples and [Gradle's API and implementation separation](https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_separation) for the configuration semantics.

## Testing

Name test classes `*Test` and give test methods descriptive names, such as `testConversionBetweenLongAndBigEndianBytes`. Use `kotlin.test` for assertions and Kotest for property tests in `core`. Cover shared behavior in `src/commonTest/kotlin` and platform behavior in the corresponding target test source set. JVM-only modules use `src/test/kotlin`.

- In multiplatform modules, use the relevant target task, such as `./gradlew :kotlin-common-core:jvmTest`. Use `jsBrowserTest` or `wasmJsBrowserTest` for browser targets and `allTests` for the configured targets supported by the host.
- `./gradlew :kotlin-common-vertx:test` runs the JVM-only Vert.x suite.

## Commits and Pull Requests

Use short, imperative commit subjects, such as `Require consumers to declare Testcontainers dependencies`. In pull requests, explain the change and its motivation, link related issues or discussions when available, and report the checks you ran. Flag intentional API changes so reviewers can inspect the ABI diff.
