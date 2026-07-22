import com.huanshankeji.gitversioning.devCommitVersionProvider

// extracted into a separate script so the version can be set before `dokka-convention`

// On CI check jobs, pin a stable version so configuration-cache entries are not invalidated
// by `git` output changing on every commit (devCommitVersionProvider). Publish must leave
// GRADLE_CI_CONFIGURATION_CACHE_STABLE_VERSION unset so snapshots keep commit versions.
version = providers.environmentVariable("GRADLE_CI_CONFIGURATION_CACHE_STABLE_VERSION")
    .orElse(providers.devCommitVersionProvider(projectBaseVersion))
    .get()
