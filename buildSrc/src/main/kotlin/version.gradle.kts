import com.huanshankeji.gitversioning.devCommitVersionProvider

// extracted into a separate script so the version can be set before `dokka-convention`

version = providers.devCommitVersionProvider(projectBaseVersion).get()
