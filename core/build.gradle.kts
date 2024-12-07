plugins {
    id("io.github.goooler.shadow") version "8.1.8"
    java
}

java {
    val jv = JavaVersion.toVersion(javaVersion)
    sourceCompatibility = jv
    targetCompatibility = jv
    if (JavaVersion.current() < jv) {
        toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

tasks.compileJava.configure {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:${annotationsVersion}")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("org.bstats:bstats-base:${statsVersion}")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveClassifier.set("")

    manifest {
        attributes["Enable-Debug"] = if (debugMode) "true" else "false"
    }

    // This may be a problem if Gson is not present on a target platform, otherwise
    // this saves a lot of space.
    exclude("com/google/gson")

    // Library relocations
    val libPkg = "io.github.wasabithumb.xclaim.shadow"
    relocate("com.moandjiezana.toml", "${libPkg}.toml")
    relocate("org.reflections", "${libPkg}.reflections")
    relocate("org.bstats", "${libPkg}.bstats")
    relocate("com.github.benmanes.caffeine", "${libPkg}.caffeine")
    relocate("org.sqlite", "${libPkg}.sqlite")
}

artifacts {
    add("shadow", tasks.shadowJar)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
