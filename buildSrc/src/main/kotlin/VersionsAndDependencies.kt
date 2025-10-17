import com.huanshankeji.CommonDependencies
import com.huanshankeji.CommonGradleClasspathDependencies
import com.huanshankeji.CommonVersions

val projectVersion = "0.7.0-SNAPSHOT"

// TODO remove Exposed's explicit version when migration to Exposed 1.0.0 is complete
// TODO remove Vert.x's explicit version when migration to Vert.x 5 is complete
// Kotest 6 requires Java 11
val commonVersions = CommonVersions(exposed = "0.61.0", vertx = "4.5.21", kotest = "5.9.1")
val commonDependencies = CommonDependencies(commonVersions)
val commonGradleClasspathDependencies = CommonGradleClasspathDependencies(commonVersions)

object DependencyVersions {
    val protobufPlugin = "0.9.5"
    val protobuf = "4.31.1"
    val okio = "3.12.0"
}
