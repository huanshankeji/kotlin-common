import com.huanshankeji.gitversioning.projectVersionFromGitProvider

// extracted into a separate script so the version can be set before `dokka-convention`

version = projectVersionFromGitProvider(projectBaseVersion).get()
