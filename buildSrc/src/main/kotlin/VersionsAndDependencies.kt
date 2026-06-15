import com.huanshankeji.CommonDependencies
import com.huanshankeji.CommonGradleClasspathDependencies
import com.huanshankeji.CommonVersions

val projectBaseVersion = "0.8.0"

// Published from local gradle-common on branch improve-cross-repo-dependency
val gradleCommonPluginsVersion =
    "0.12.0-dev-commit-0bca1ba009f63b52ae3d5fe1b4cc5a466b8b61b5"

val commonVersions = CommonVersions(kotest = "6.1.11")
val commonDependencies = CommonDependencies(commonVersions)
val commonGradleClasspathDependencies = CommonGradleClasspathDependencies(commonVersions)

object DependencyVersions {
    // https://github.com/google/protobuf-gradle-plugin/releases
    val protobufPlugin = "0.9.5"

    // https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java
    val protobuf = "4.34.1"

    // https://github.com/square/okio/tags
    val okio = "3.17.0"
}
