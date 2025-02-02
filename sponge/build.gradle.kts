import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    java
    id("org.spongepowered.gradle.plugin") version "2.2.0"
    id("io.github.goooler.shadow") version "8.1.8"
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
    implementation(project(":core"))

    compileOnly("org.jetbrains:annotations:${annotationsVersion}")
    implementation("org.bstats:bstats-sponge:${statsVersion}")

    // TODO: Integrations
}

sponge {
    apiVersion("9.1.0-SNAPSHOT")
    license("Apache-2.0")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("xclaim") {
        version("${project.version}")
        displayName("XClaim")
        description("A fully-featured chunk claiming system for community servers")
        entrypoint("io.github.wasabithumb.xclaim.XClaimPlugin")
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

tasks.shadowJar {
    archiveClassifier.set("")

    manifest {
        attributes["Enable-Debug"] = "$debugMode"
    }

    val libPkg = "io.github.wasabithumb.xclaim.shadow"
    relocate("org.bstats", "${libPkg}.bstats")
    // TODO: Library exclusions & relocations
}

artifacts {
    add("default", tasks.shadowJar)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
