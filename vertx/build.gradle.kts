plugins {
    id("kotlin-jvm-conventions")
}

dependencies {
    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    implementation("io.vertx:vertx-core")
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-sql-client")
    implementation("io.vertx:vertx-lang-kotlin")
    implementation("io.vertx:vertx-lang-kotlin-coroutines")

    // TODO: use Maven coordinates?
    implementation(project(":core"))
    implementation(project(":coroutines"))
}
