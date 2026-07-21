import com.huanshankeji.team.ShreckYe
import com.huanshankeji.team.setUpPomForTeamDefaultOpenSource

plugins {
    id("com.huanshankeji.team.with-group")
    id("com.huanshankeji.team.gitversioning.opensourceconvention.githubpackages.publish")
    id("version")
    id("dokka-convention")
}

gitVersioningOpenSourceConventionGithubPackagesPublish {
    signAllPublicationsIfRelease(isRelease)
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
