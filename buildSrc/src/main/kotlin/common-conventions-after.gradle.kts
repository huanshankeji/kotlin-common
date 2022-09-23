import com.huanshankeji.team.`Shreck Ye`
import com.huanshankeji.team.pomForTeamDefaultOpenSource

plugins {
    `maven-publish`
    id("com.huanshankeji.team.default-github-packages-maven-publish")
}

publishing.publications.withType<MavenPublication> {
    pomForTeamDefaultOpenSource(
        project,
        "Huanshankeji Kotlin Common",
        "Huanshankeji's common code libraries in Kotlin"
    ) {
        `Shreck Ye`()
    }
}
