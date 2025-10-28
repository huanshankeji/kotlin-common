import com.huanshankeji.team.ShreckYe
import com.huanshankeji.team.setUpPomForTeamDefaultOpenSource

plugins {
    id("com.huanshankeji.team.with-group")
    id("maven-central")
    id("com.huanshankeji.team.default-github-packages-maven-publish")
    id("version")
    id("dokka-convention")
    id("com.huanshankeji.maven-central-publish-conventions")
}

mavenPublishing.pom {
    setUpPomForTeamDefaultOpenSource(
        project,
        "Huanshankeji Kotlin Common",
        "Huanshankeji's common code libraries in Kotlin",
        "2022"
    ) {
        ShreckYe()
    }
}
