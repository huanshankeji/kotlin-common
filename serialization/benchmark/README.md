# Kotlin Common Serialization Benchmark

This module contains benchmarks for the Kotlin Common Serialization library.

## Known Issues

### Kotlin 2.2+ Metadata Compilation Warning

When running Gradle commands, you may see the following warning:

```
Warning: Unsupported compilation 'compilation 'main' (target metadata (common))', ignoring.
```

**This warning is expected and harmless.** It occurs because:

1. Kotlin 2.2+ automatically creates a metadata compilation for multiplatform common code
2. The kotlinx-benchmark plugin (v0.4.14) doesn't yet recognize this compilation type  
3. The plugin correctly handles this by ignoring the unsupported compilation

**Impact:** None - the warning is cosmetic only. Builds and benchmarks work correctly.

**Resolution:** The warning will disappear when:
- kotlinx-benchmark releases a Kotlin 2.2+ compatible version, OR
- The gradle-plugins convention is updated to filter metadata compilations

For more details, see:
- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [kotlinx.benchmark GitHub](https://github.com/Kotlin/kotlinx-benchmark)

## Running Benchmarks

### JVM Benchmarks
```bash
./gradlew :kotlin-common-serialization:kotlin-common-serialization-benchmark:jvmBenchmark
```

### JS Benchmarks  
```bash
./gradlew :kotlin-common-serialization:kotlin-common-serialization-benchmark:jsBenchmark
```

### All Benchmarks
```bash
./gradlew :kotlin-common-serialization:kotlin-common-serialization-benchmark:benchmark
```
