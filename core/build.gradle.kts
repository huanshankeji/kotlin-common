plugins {
    id("kotlin-multiplatform-conventions")
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}
