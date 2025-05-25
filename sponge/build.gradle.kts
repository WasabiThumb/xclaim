import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("org.spongepowered.gradle.plugin") version "2.3.0"
    id("io.github.goooler.shadow") version "8.1.8"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))

    compileOnly(libs.annotations)
    implementation(libs.bstats.sponge)

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
        attributes["Enable-Debug"] = "${hasProperty("enableDebug")}"
    }

    // Library exclusions (present in Sponge)
    dependencies {
        exclude(dependency("com.google.code.gson:gson"))
        exclude(dependency("org.checkerframework:checker-qual"))
        exclude(dependency("org.slf4j:slf4j-api"))
    }

    // Library relocations
    val libPkg = "io.github.wasabithumb.xclaim.shadow"
    relocate("io.github.wasabithumb.jtoml", "${libPkg}.jtoml")
    relocate("org.reflections", "${libPkg}.reflections")
    relocate("org.bstats", "${libPkg}.bstats")
    relocate("com.github.benmanes.caffeine", "${libPkg}.caffeine")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
