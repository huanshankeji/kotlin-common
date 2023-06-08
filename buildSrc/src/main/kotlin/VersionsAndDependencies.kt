import com.huanshankeji.CommonDependencies
import com.huanshankeji.CommonGradleClasspathDependencies
import com.huanshankeji.CommonVersions

val projectVersion = "0.4.0-SNAPSHOT"
val commonVersions =
    CommonVersions(kotlinxSerialization = "1.5.1", vertx = "4.4.2") // TODO: use the default version when it's bumped
val commonDependencies = CommonDependencies(commonVersions)
val commonGradleClasspathDependencies = CommonGradleClasspathDependencies(commonVersions)
