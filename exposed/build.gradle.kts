import com.huanshankeji.cpnProject

plugins {
    id("jvm-conventions")
}

dependencies {
    implementation(cpnProject(project, ":core")) // for the `@Untested` annotation

    implementation(commonDependencies.exposed.core())
    testImplementation(kotlin("test"))
}
