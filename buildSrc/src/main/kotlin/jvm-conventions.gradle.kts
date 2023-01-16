plugins {
    id("common-conventions-before")
    id("com.huanshankeji.kotlin-jvm-library-sonatype-ossrh-publish-conventions")
    id("common-conventions-after")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))
