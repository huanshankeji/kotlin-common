import com.huanshankeji.CommonDependencies
import com.huanshankeji.CommonGradleClasspathDependencies
import com.huanshankeji.CommonVersions

val projectVersion = "0.6.0-kotlin-2.1.0-SNAPSHOT"

val commonVersions = CommonVersions(arrow = "2.0.0-alpha.4" /* for Wasm JS*/, exposed = "0.56.0")
val commonDependencies = CommonDependencies(commonVersions)
val commonGradleClasspathDependencies = CommonGradleClasspathDependencies(commonVersions)

object DependencyVersions {
    val protobufPlugin = "0.9.4"
    val protobuf = "3.25.2" // Not bumped. The version is a bit chaotic.
    val kotlinxIo = "0.5.4"
    val okio = "3.9.1"
}
