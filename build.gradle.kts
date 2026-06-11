import com.huanshankeji.cpnProject

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

plugins {
    id("org.jetbrains.dokka")
    id("com.huanshankeji.team.root-project-conventions")
}

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
