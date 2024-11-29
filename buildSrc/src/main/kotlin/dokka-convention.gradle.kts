plugins {
    id("com.huanshankeji.team.dokka.github-dokka-convention")
}

dokka {
    //moduleName.set("Huanshankeji Kotlin Common")
    dokkaSourceSets.all {
        //includes.from("README.md")
        /*pluginsConfiguration.html {
            footerMessage.set("(c) Yongshun Ye")
        }*/
    }
}
