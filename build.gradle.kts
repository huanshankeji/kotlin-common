import com.huanshankeji.cpnProject

plugins {
    id("com.huanshankeji.root-project-conventions")
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
    ).forEach {
        dokka(cpnProject(project, ":$it"))
    }
}
