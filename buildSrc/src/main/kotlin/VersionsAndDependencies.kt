import com.huanshankeji.CommonDependencies
import com.huanshankeji.CommonGradleClasspathDependencies
import com.huanshankeji.CommonVersions

val projectVersion = "0.7.0"

// Kotest 6 requires Java 11
val commonVersions = CommonVersions(kotest = "5.9.1")
val commonDependencies = CommonDependencies(commonVersions)
val commonGradleClasspathDependencies = CommonGradleClasspathDependencies(commonVersions)

object DependencyVersions {
    // https://github.com/google/protobuf-gradle-plugin/releases
    val protobufPlugin = "0.9.5"

    // https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java
    val protobuf = "4.33.0"

    // https://github.com/square/okio/tags
    val okio = "3.16.2"
}
