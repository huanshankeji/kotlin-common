import com.huanshankeji.CommonDependencies
import com.huanshankeji.CommonGradleClasspathDependencies
import com.huanshankeji.CommonVersions

val projectVersion = "0.6.1"

val commonVersions = CommonVersions()
val commonDependencies = CommonDependencies(commonVersions)
val commonGradleClasspathDependencies = CommonGradleClasspathDependencies(commonVersions)

object DependencyVersions {
    val protobufPlugin = "0.9.4"
    val protobuf = "3.25.2" // Not bumped. The version is a bit chaotic.
    val kotlinxIo = "0.5.4"
    val okio = "3.9.1"
}
