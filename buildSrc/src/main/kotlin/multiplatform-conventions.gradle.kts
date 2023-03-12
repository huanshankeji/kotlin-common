plugins {
    id("common-conventions-before")
    id("com.huanshankeji.kotlin-multiplatform-jvm-and-js-browser-conventions")
    id("com.huanshankeji.kotlin-multiplatform-sonatype-ossrh-publish-conventions")
    id("common-conventions-after")
}

kotlin.jvmToolchain(8)
