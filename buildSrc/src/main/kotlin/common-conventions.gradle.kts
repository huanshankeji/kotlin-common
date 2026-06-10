import com.huanshankeji.team.ShreckYe
import com.huanshankeji.team.setUpPomForTeamDefaultOpenSource
import org.gradle.plugins.signing.Sign

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

// solution by AI agent
// GitHub Packages does not require signed artifacts.
gradle.taskGraph.whenReady {
    val publishingToGitHubPackages = allTasks.any {
        it.name == "publishAllPublicationsToGitHubPackagesRepository" ||
            it.name.endsWith("PublicationToGitHubPackagesRepository")
    }
    val publishingToMavenCentral = allTasks.any {
        it.name == "publishAllPublicationsToMavenCentralRepository" ||
            it.name == "publishAndReleaseToMavenCentral" ||
            it.name.endsWith("PublicationToMavenCentralRepository")
    }
    if (publishingToGitHubPackages && !publishingToMavenCentral) {
        allTasks.filterIsInstance<Sign>().forEach { it.enabled = false }
    }
}
