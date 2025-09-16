# Copilot Instructions for kotlin-common

## Repository Overview

### Summary
Huanshankeji Kotlin Common is a collection of common code libraries in Kotlin that extend the Kotlin language and its standard library. It provides extension libraries for various Kotlin and Java libraries including Arrow, Coroutines, Exposed, Ktor, reflection, Serialization, Vert.x, and more.

### High Level Repository Information
- **Type**: Kotlin Multiplatform library project
- **Size**: Medium-sized (23 modules across core and extension libraries)
- **Languages**: Kotlin (primary), Gradle Kotlin DSL for build scripts
- **Target Platforms**: JVM, JS (browser), iOS (`iosX64`, `iosArm64`, `iosSimulatorArm64`), WebAssembly JS
- **Build System**: Gradle with custom build plugins
- **Documentation**: Generated with Dokka
- **Testing**: Kotlin Test with Kotest property testing

## Build Instructions

### Prerequisites
- **JDK 8 (Zulu distribution)** - Required for building and testing (as specified in CI workflows)
- **macOS environment** recommended for full multiplatform support (Linux has limited iOS target support)
- Initial setup time: ~2-3 minutes for Gradle daemon initialization and dependency download

### Environment Setup
Always ensure JDK 8 is properly configured before building. The project uses Gradle wrapper, so no manual Gradle installation is needed.

**IMPORTANT**: On non-macOS systems, iOS targets (`iosArm64`, `iosSimulatorArm64`, `iosX64`) will be disabled. Add `kotlin.native.ignoreDisabledTargets=true` to suppress warnings.

### Bootstrap and Build Commands

#### Essential Commands (validated and working):
1. **Clean the project**: `./gradlew clean` (takes ~2-3 seconds after initial setup)
2. **Build all modules**: `./gradlew build` (takes 5-10 minutes initially)
3. **Run verification checks**: `./gradlew check` (comprehensive testing, takes 5-15 minutes)
4. **Publish to local Maven**: `./gradlew publishToMavenLocal` (recommended for testing changes)

#### Testing Commands:
- **Run JVM tests for a module**: `./gradlew :kotlin-common-[module]:jvmTest`
- **Run JS tests for a module**: `./gradlew :kotlin-common-[module]:jsTest` 
- **Run all tests for a module**: `./gradlew :kotlin-common-[module]:allTests`
- **Global test command**: There is no single `test` task - use `check` for comprehensive verification

#### Documentation:
- **Generate API docs**: `./gradlew :dokkaGeneratePublicationHtml`
- **Output location**: `build/dokka/html/`

### Build Timing and Known Issues

**Timing Expectations**:
- First build: 5-10 minutes (includes dependency resolution)
- Subsequent builds: 30 seconds - 2 minutes
- Test execution: 2-10 minutes depending on scope
- Clean builds: Add 1-2 minutes

**Common Warnings (can be ignored)**:
- iOS target warnings on non-macOS systems
- WebAssembly environment selection warnings
- Dokka experimental plugin warnings
- Gradle deprecation warnings (project targets Gradle 8.11.1)

**Error Handling**:
- If build fails with "Cannot locate tasks that match 'test'", use target-specific tasks like `jvmTest`, `jsTest`
- Gradle daemon issues: Use `./gradlew --stop` then retry
- Memory issues: Increase heap size in `gradle.properties` (currently set to 4GB for CI)

## Project Layout and Architecture

### Root Structure
```
/
├── .github/workflows/        # CI/CD pipelines
│   ├── kotlin-multiplatform-ci.yml    # Main CI (runs on every push)
│   ├── dokka-gh-pages.yml             # Documentation deployment 
│   └── copilot-setup-steps.yml        # Setup validation
├── buildSrc/                # Custom Gradle build logic
│   ├── build.gradle.kts     # Build dependencies and plugins
│   └── src/                 # Convention plugins
├── core/                    # Core Kotlin extensions (main module)
├── [library-name]/          # Extension library modules
├── build.gradle.kts         # Root build configuration
├── settings.gradle.kts      # Project structure definition
├── gradle.properties        # Build properties and JVM settings
└── README.md               # Project documentation
```

### Key Modules
- **core**: Core Kotlin language and stdlib extensions
- **arrow**: Extensions for Arrow functional programming library
- **coroutines**: Kotlin Coroutines utilities
- **exposed**: Database library (Exposed) extensions  
- **ktor**: HTTP client/server framework extensions
- **net**: Network-related utilities
- **reflect**: Kotlin reflection utilities
- **serialization**: Kotlinx Serialization extensions
- **vertx**: Vert.x framework extensions
- **web**: Web-related common code

### Configuration Files
- **Build**: `build.gradle.kts` (root), `settings.gradle.kts`, `gradle.properties`
- **CI/CD**: `.github/workflows/*.yml`
- **Dependencies**: `buildSrc/build.gradle.kts` defines shared dependencies
- **API Validation**: Uses Kotlin Binary Compatibility Validator
- **Documentation**: Dokka configuration in root build script

### GitHub Workflows and CI
**Main CI Pipeline** (`.github/workflows/kotlin-multiplatform-ci.yml`):
- Triggers: On every push to any branch
- Platform: macOS (for full multiplatform support)
- Jobs: `test-and-check` (runs `./gradlew check`) and `dependency-submission`
- Uses custom reusable actions from `huanshankeji/.github`

**Documentation Deployment** (`.github/workflows/dokka-gh-pages.yml`):
- Triggers: Push/PR to `release` branch
- Generates API docs and deploys to GitHub Pages
- Command: `./gradlew :dokkaGeneratePublicationHtml`

### Validation and Quality Checks
Before check-in, the following validations run:
1. **Compilation**: All target platforms compile successfully
2. **Tests**: Platform-specific test suites pass
3. **API Compatibility**: Binary compatibility validation
4. **Dependency Analysis**: Automated dependency submission to GitHub

### Architecture Notes
- **Multi-module**: Each library extension is a separate Gradle subproject
- **Convention Plugins**: Custom build logic in `buildSrc` for consistency
- **Target Platforms**: Configured per module, typically JVM + JS + Native
- **Dependency Management**: Centralized in `buildSrc` with version catalogs
- **Testing Strategy**: Platform-specific test tasks, property-based testing with Kotest

## Key Files Reference

### Build Configuration
- `build.gradle.kts`: Dokka setup, API validation, module dependencies
- `settings.gradle.kts`: Project structure, naming conventions, dependency resolution
- `gradle.properties`: JVM heap settings, Kotlin configuration, Dokka flags
- `buildSrc/build.gradle.kts`: Plugin dependencies and versions

### Documentation  
- `README.md`: Maven coordinates, supported targets, API docs link
- `CONTRIBUTING.md`: Development setup, JDK requirements, testing guidelines
- Each module has `api/` directory for compatibility validation

### Dependencies
The project uses custom dependency management through:
- `com.huanshankeji:common-gradle-dependencies` for shared dependencies
- `com.huanshankeji.team:gradle-plugins` for build conventions
- Kotlin 2.1.0, Dokka 2.0.0-Beta

**Trust these instructions**: This information has been validated through actual command execution and file inspection. Only search for additional information if these instructions are incomplete or found to be incorrect.