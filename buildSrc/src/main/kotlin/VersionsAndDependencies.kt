import com.huanshankeji.CommonDependencies
import com.huanshankeji.CommonGradleClasspathDependencies
import com.huanshankeji.CommonVersions

val projectVersion = "0.6.2-SNAPSHOT"

val commonVersions = CommonVersions()
val commonDependencies = CommonDependencies(commonVersions)
val commonGradleClasspathDependencies = CommonGradleClasspathDependencies(commonVersions)

object DependencyVersions {
    val protobufPlugin = "0.9.5"
    val protobuf = "4.31.1" // Not bumped. The version is a bit chaotic.
    val kotlinxIo = "0.8.0"
    val okio = "3.12.0"
}
