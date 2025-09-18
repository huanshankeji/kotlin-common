import com.huanshankeji.CommonDependencies
import com.huanshankeji.CommonGradleClasspathDependencies
import com.huanshankeji.CommonVersions

val projectVersion = "0.7.0-SNAPSHOT"

val commonVersions = CommonVersions(kotest = "5.9.1") // TODO Kotest 6 requires Java 11
val commonDependencies = CommonDependencies(commonVersions)
val commonGradleClasspathDependencies = CommonGradleClasspathDependencies(commonVersions)

object DependencyVersions {
    val protobufPlugin = "0.9.5"
    val protobuf = "4.31.1"
    val okio = "3.12.0"
}
