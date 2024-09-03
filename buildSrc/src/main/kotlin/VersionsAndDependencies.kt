import com.huanshankeji.CommonDependencies
import com.huanshankeji.CommonGradleClasspathDependencies
import com.huanshankeji.CommonVersions

val projectVersion = "0.5.0-SNAPSHOT"

val commonVersions = CommonVersions(arrow = "2.0.0-alpha.3") // for Wasm JS
val commonDependencies = CommonDependencies(commonVersions)
val commonGradleClasspathDependencies = CommonGradleClasspathDependencies(commonVersions)

object DependencyVersions {
    val protobufPlugin = "0.9.4"
    val protobuf = "3.25.2"
}
