import com.huanshankeji.cpnProject

plugins {
    id("jvm-conventions")
}

dependencies {
    api(cpnProject(project, ":net"))

    with(commonDependencies.testcontainers) {
        api(platformBom())
        api(testcontainers)
        api(testcontainersPostgresql)
    }
}
