# Change log

## v0.6.1 / 2024-12-05

* bump Kotlin to 2.1.0 and our library and build dependencies to the latest (including pre-release versions except for Vert.x 5)
* deprecate `PgPoolOptions.setUpConventionally`, which should've been included in v0.6.0 but was not

## v0.6.0 / 2024-11-29

* add conversion functions to convert Vert.x `Buffer`s to kotlinx-io `RawSink`s and `Sink`s and Okio `Sink`s and `BufferedSink`s

   The `Source` conversion functions are not provided because Vert.x `Buffer` doesn't provide reading methods with a reader index.

* adapt to the [Exposed SELECT DSL design changes](https://github.com/JetBrains/Exposed/pull/1916) and bump Exposed to v0.56.0

   The old `deleteWhereStatement` that conflicts with the new one is removed, causing a source and binary incompatible change.

* add API documentation generated by Dokka hosted at <https://huanshankeji.github.io/kotlin-common/>
* add CODE_OF_CONDUCT.md and CONTRIBUTING.md
* use the Kotlin binary compatibility validator
* add some experimental plus operators for nullable functions/lambdas
* add some common functions for Exposed and Vert.x SQL Client such as `jdbcUrl`, `ClientBuilder<*>.withCoConnectHandler`, and `SqlConnectOptions.setUpConventionally`
* add a `CoroutineAutoCloseable` interface like `AutoCloseable` and its version of the `use` extension function

## v0.5.1 / 2024-10-19

* enable the iOS targets for the "coroutines" module, which was disabled due to a compiler bug

## v0.5.0 / 2024-10-19

* support the iOS and Wasm JS targets
* add the enum operator functions `+`, `-`, `++`, `--` since `enumEntries` got supported in Kotlin 2.0
* add `MutableMapStringKeyValueStore`
* add more functions for checking OSs in `com.huanshankeji.os`
* add some `isSorted...` extension functions that the standard library lacks
