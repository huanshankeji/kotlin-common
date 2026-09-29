import com.huanshankeji.cpnProject
import com.huanshankeji.gitversioning.devCommitOrReleaseVersionProvider

plugins {
    id("com.huanshankeji.root-project-conventions")
    id("org.jetbrains.dokka")
    id("dokka-convention")
}

version = providers.devCommitOrReleaseVersionProvider(projectBaseVersion, isRelease).get()

dependencies {
    listOf(
        "core",
        "net",
        "web",

        "arrow",
        "coroutines",
        "exposed",
        "ktor:client",
        "reflect",
        "serialization",
        "vertx",
    ).forEach {
        dokka(cpnProject(project, ":$it"))
    }
}
