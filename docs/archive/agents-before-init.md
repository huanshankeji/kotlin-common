**Must load and read** the org baseline first; this `AGENTS.md` is incomplete without it: [@huanshankeji/.github general agent instructions](https://github.com/huanshankeji/.github/blob/main/docs/general-agent-instructions.md). A skipped or failed load, or an unread document, is a blocking error until the document is in context and has been read.

## Dependency policy

This project provides **extension libraries**. Consumers are expected to explicitly declare the original upstream libraries they use, alongside our extension modules.

- Declare upstream library dependencies with `implementation`, including when upstream types appear in public extension receivers, parameters, return types, or supertypes. This is a deliberate project policy; a public upstream type alone is not a reason to change a dependency to `api`.
- Apply the same rule to upstream BOMs and feature variants: use `implementation(platform(...))` and the feature's `Implementation` configuration, such as `postgresqlImplementation` or `vertxWebImplementation`.
- Dependencies between this project's own modules may use `api` when intentionally re-exporting our own public types. For example, `testcontainers` exposes `:net` because `hostAndPort()` returns `HostAndPort`.

`implementation` keeps upstream dependencies off consumers' compile classpaths; on the JVM, they still participate in runtime dependency resolution. Consumers declare the upstream modules and any BOMs needed to compile their code. See [Gradle's API and implementation separation](https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_separation) for the configuration semantics.
