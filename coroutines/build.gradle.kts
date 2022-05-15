import com.huanshankeji.kotlinx

plugins {
    id("kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlinx("coroutines-core", kotlinCoroutinesVersion))
            }
        }
    }
}
