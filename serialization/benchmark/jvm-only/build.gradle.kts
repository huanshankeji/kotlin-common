import com.huanshankeji.SourceSetType
import com.huanshankeji.cpnProject

plugins {
    id("com.huanshankeji.kotlin-jvm-common-conventions")
    id("com.huanshankeji.benchmark.kotlinx-benchmark-jvm-conventions")
    id("com.google.protobuf") version DependencyVersions.protobufPlugin
}

kotlinxBenchmarkConventions {
    sourceSetType.set(SourceSetType.Main)
}

dependencies {
    implementation(cpnProject(project, ":serialization:benchmark"))
    implementation("com.google.protobuf:protobuf-javalite:${DependencyVersions.protobuf}")
}

private val PROTOBUF_DEFAULT = "protobufDefault"
sourceSets.create(PROTOBUF_DEFAULT)
dependencies {
    val implementationString = "${PROTOBUF_DEFAULT}Implementation"
    implementationString(with(sourceSets.main.get()) { output + runtimeClasspath })
    implementationString("com.google.protobuf:protobuf-java:${DependencyVersions.protobuf}")
}

private val PROTOBUF_LITE = "protobufLite"
sourceSets.create(PROTOBUF_LITE)
dependencies {
    val implementationString = "${PROTOBUF_LITE}Implementation"
    implementationString(with(sourceSets.main.get()) { output + runtimeClasspath })
    implementationString("com.google.protobuf:protobuf-javalite:${DependencyVersions.protobuf}")
}

private val MAIN = "main"

benchmark.targets {
    register(PROTOBUF_DEFAULT)
    register(PROTOBUF_LITE)
}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${DependencyVersions.protobuf}"
    }
    generateProtoTasks.all().configureEach {
        when (sourceSet.name) {
            PROTOBUF_LITE ->
                builtins.named("java") {
                    option("lite")
                }
        }
    }
}
