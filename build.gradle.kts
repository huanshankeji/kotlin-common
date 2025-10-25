import com.huanshankeji.cpnProject

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

plugins {
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.1"
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

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }

    ignoredProjects += "kotlin-common".let {
        "$it-serialization-benchmark".let {
            listOf(it, "$it-jvm-only")
        } + "$it-vertx-with-context-receivers"
    }
}
