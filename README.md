# Huanshankeji Kotlin Common

[![Maven Central](https://img.shields.io/maven-central/v/com.huanshankeji/kotlin-common-core)](https://search.maven.org/search?q=g:com.huanshankeji%20a:kotlin-common-*)
![Kotlin version](https://kotlin-version.aws.icerock.dev/kotlin-version?group=com.huanshankeji&name=kotlin-common-core)

Huanshankeji's common code libraries in Kotlin

These include a core library to extend the Kotlin language and its standard library, and extension libraries for various Kotlin and Java libraries such as [Λrrow](https://arrow-kt.io/), Coroutines ([docs here](https://kotlinlang.org/docs/coroutines-overview.html) and [repository here](https://github.com/Kotlin/kotlinx.coroutines)), [Exposed](https://github.com/JetBrains/Exposed), [Ktor](https://ktor.io/), [reflection](https://kotlinlang.org/docs/reflection.html), Serialization ([docs here](https://kotlinlang.org/docs/serialization.html) and [repository here](https://github.com/Kotlin/kotlinx.serialization)), [Testcontainers](https://testcontainers.com/), [Vert.x](https://vertx.io/) (along with extensions for [kotlinx-io](https://github.com/Kotlin/kotlinx-io) and [Okio](https://square.github.io/okio/)), etc. For common extensions for Compose Multiplatform, check out [compose-multiplatform-common](https://github.com/huanshankeji/compose-multiplatform-html-unified/tree/main/common).

Currently supported targets by multiplatform libraries: JVM, JS (browser), iOS (`iosX64`, `iosArm64`, and `iosSimulatorArm64`), and Wasm JS.

[Check out the API documentation here.](https://huanshankeji.github.io/kotlin-common/)

## Maven coordinates

```kotlin
"com.huanshankeji:kotlin-common-$module:$version"
```

### Declare upstream dependencies explicitly

These are **extension libraries**: consumers are expected to explicitly declare the original upstream libraries they use alongside the corresponding `kotlin-common` modules. For example:

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("com.huanshankeji:kotlin-common-coroutines:$version")
}
```

Our modules deliberately use `implementation` for upstream dependencies, even when upstream types appear in public extension receivers, parameters, or return types. They do not re-export those libraries onto consumers' compile classpaths. On the JVM, these dependencies still participate in runtime dependency resolution. Declare the upstream modules and any BOMs needed by your code in the relevant source set or configuration.
