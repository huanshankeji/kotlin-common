plugins {
    id("jvm-conventions")
}

dependencies {
    implementation(commonDependencies.exposed.core())
    testImplementation(kotlin("test"))
}
