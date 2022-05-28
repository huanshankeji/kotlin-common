import com.huanshankeji.CommonDependencies
import com.huanshankeji.DefaultVersions

plugins {
    id("multiplatform-conventions")
}

kotlin.sourceSets {
    all {
        languageSettings.optIn("kotlin.RequiresOptIn")
    }

    val commonTest by getting {
        dependencies {
            implementation(kotlin("test"))
            implementation("io.kotest:kotest-property:${DefaultVersions.kotest}")
            implementation(CommonDependencies.Kotlinx.Coroutines.test())
        }
    }
}
