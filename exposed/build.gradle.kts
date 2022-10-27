plugins {
    id("jvm-conventions")
}

dependencies {
    implementation(commonDependencies.exposed.core())
    // TODO: remove
    //implementation("com.google.guava:guava:${DependencyVersions.guava}")
}
