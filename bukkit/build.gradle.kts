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
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(project(":core"))
    compileOnly("org.jetbrains:annotations:${annotationsVersion}")
    implementation("org.bstats:bstats-bukkit:${statsVersion}")
    compileOnly("org.spigotmc:spigot-api:${serverVersion}")
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveClassifier.set("")

    val libPkg = "io.github.wasabithumb.xclaim.shadow"
    relocate("org.bstats", "${libPkg}.bstats")
}

artifacts {
    add("shadow", tasks.shadowJar)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
