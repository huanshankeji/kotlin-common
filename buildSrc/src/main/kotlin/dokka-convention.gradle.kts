plugins {
    id("org.jetbrains.dokka")
}

dokka {
    //moduleName.set("Huanshankeji Kotlin Common")
    dokkaSourceSets.all {
        //includes.from("README.md")
        sourceLink {
            //localDirectory.set(file("src/main/kotlin"))
            remoteUrl(
                "https://github.com/huanshankeji/kotlin-common/tree/v${version}/${
                    with(project) {
                        name.removePrefix(parent!!.name + '-')
                    }
                }"
            )
            remoteLineSuffix.set("#L")
        }
        /*pluginsConfiguration.html {
            footerMessage.set("(c) Yongshun Ye")
        }*/
    }
}
