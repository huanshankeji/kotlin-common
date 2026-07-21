import com.huanshankeji.gitversioning.devCommitOrReleaseVersionProvider

// extracted into a separate script so the version can be set before `dokka-convention`

version = providers.devCommitOrReleaseVersionProvider(projectBaseVersion, isRelease).get()
