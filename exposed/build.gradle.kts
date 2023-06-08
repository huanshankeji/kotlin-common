import com.huanshankeji.cpnProject

plugins {
    id("jvm-conventions")
}

dependencies {
    implementation(commonDependencies.exposed.core())

    implementation(cpnProject(project, ":core"))
    implementation(cpnProject(project, ":reflect"))
}
