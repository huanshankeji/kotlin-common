plugins {
    `maven-publish`
    id("com.huanshankeji.team.default-github-packages-publish")
}

publishing.publications.withType<MavenPublication> {
    pom {
        name.set("Huanshankeji Kotlin Common")
        description.set("Huanshankeji's common code libraries in Kotlin")
        val githubUrl = "https://github.com/huanshankeji/kotlin-common"
        url.set(githubUrl)

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("ShreckYe")
                name.set("Shreck Ye")
                email.set("ShreckYe@gmail.com")
            }
        }
        scm {
            val scmString = "scm:git:$githubUrl.git"
            connection.set(scmString)
            developerConnection.set(scmString)
            url.set(githubUrl)
        }
    }
}
