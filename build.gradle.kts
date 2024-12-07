
allprojects {
    group = "io.github.wasabithumb"
    version = "2.0.0"
}

subprojects {
    tasks.withType(Jar::class.java) {
        archiveBaseName.set("${project.rootProject.name}-${project.name}")
    }
}

debugMode = hasProperty("enableDebug")
javaVersion = 17
annotationsVersion = "24.1.0"
mcVersion = "1.18.2"
serverVersion = "${mcVersion}-R0.1-SNAPSHOT"
statsVersion = "3.1.0"
