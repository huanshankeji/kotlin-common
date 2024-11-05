import com.huanshankeji.cpnProject

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

plugins {
    id("org.jetbrains.dokka")
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
        //"vertx:with-context-receivers",
    ).forEach {
        dokka(cpnProject(project, ":$it"))
    }
}
