import com.huanshankeji.CommonDependencies
import com.huanshankeji.CommonGradleClasspathDependencies
import com.huanshankeji.CommonVersions

val projectVersion = "0.8.0-SNAPSHOT-github-packages-publish-test"

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
