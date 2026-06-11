See [Copilot instructions](.github/copilot-instructions.md) for this repository.

Organization-wide standards and open-source library map: [@huanshankeji/.github general agent instructions](https://github.com/huanshankeji/.github/blob/main/docs/general-agent-instructions.md).

## Cursor Cloud specific instructions

This is a Kotlin Multiplatform **library** project (no runnable service/app). "Running" it means building, testing, and verifying the library modules. Standard build/test/lint commands are documented in `.github/copilot-instructions.md` and `CONTRIBUTING.md` — use those (e.g. `./gradlew check`, `./gradlew :kotlin-common-<module>:jvmTest`, `./gradlew publishToMavenLocal`).

Non-obvious environment notes:

- **JDKs**: Temurin JDK 11 and 17 are installed under `/usr/lib/jvm` and Gradle auto-detects them (Common Linux Locations). The modules compile with `jvmToolchain(11)`, so a **JDK 11 toolchain must stay installed/detectable** — there is no Foojay toolchain resolver configured, so Gradle will NOT auto-download a missing JDK 11 and the build fails without it.
- **Gradle launcher JVM is JDK 17 everywhere**: `JAVA_HOME` is set to Temurin 17 in `~/.bashrc` for interactive shells, and the system default `java` (`update-alternatives`) is also Temurin 17, so the non-interactive startup update script (`./gradlew --version`) launches Gradle on the same JDK. Keep the launcher JDK consistent — launching Gradle with different JDKs spawns multiple Gradle daemons (each up to `-Xmx4G` per `gradle.properties`), risking OOM in resource-constrained agents. Compilation always uses the JDK 11 toolchain regardless of the launcher JVM.
- **Browser tests**: `./gradlew check` runs JS and Wasm JS `*BrowserTest` tasks via Karma on **headless Chrome** (`google-chrome` is installed). These pass in this environment. If Karma cannot locate the browser, export `CHROME_BIN=/usr/bin/google-chrome-stable`.
- **Module path names are concatenated** with the root name, e.g. the `core` directory is the `:kotlin-common-core` Gradle project and `ktor/client` is `:kotlin-common-ktor-client`.
- **API compatibility (`apiCheck`)**: do **not** run `apiDump` automatically just to make `check` pass (see `.github/copilot-instructions.md`); when `check` fails only on `apiCheck`, validate with `./gradlew test`/`jvmTest`/`allTests` plus `./gradlew publishToMavenLocal`. However, if the public API changed **intentionally**, the updated `api/` declaration files must be regenerated with `./gradlew apiDump` and committed — otherwise CI fails on `apiCheck`. Leave that `apiDump` + commit step to the developer reviewing the API surface (or do it yourself only once you are confident the API changes are final).
