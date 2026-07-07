import com.huanshankeji.cpnProject

plugins {
    id("org.jetbrains.dokka")
    id("com.huanshankeji.root-project-conventions")
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
