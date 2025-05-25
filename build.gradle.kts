
allprojects {
    apply(plugin = "java-library")

    group = "io.github.wasabithumb"
    version = "2.0.0"

    val targetJavaVersion = 21
    java {
        val javaVersion = JavaVersion.toVersion(targetJavaVersion)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }

    tasks.withType(JavaCompile::class) {
        options.release = targetJavaVersion
        options.encoding = "UTF-8"
    }
}

subprojects {
    tasks.withType(Jar::class.java) {
        archiveBaseName.set("${project.rootProject.name}-${project.name}")
    }
}

plugins {
    id("java-library")
}
