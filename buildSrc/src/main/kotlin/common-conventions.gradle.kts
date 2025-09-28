import com.huanshankeji.team.`Shreck Ye`
import com.huanshankeji.team.pomForTeamDefaultOpenSource

plugins {
    id("com.huanshankeji.team.with-group")
    id("maven-central")
    id("com.huanshankeji.team.default-github-packages-maven-publish")
    id("version")
    id("dokka-convention")
}

afterEvaluate {
    publishing.publications.withType<MavenPublication> {
        pomForTeamDefaultOpenSource(
            project,
            "Huanshankeji Kotlin Common",
            "Huanshankeji's common code libraries in Kotlin"
        ) {
            `Shreck Ye`()
        }
    }
}
