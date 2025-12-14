import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency
import kotlin.jvm.java

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.sponge)
}

repositories {
    mavenCentral()
    maven("https://repo.bluecolored.de/releases/")
}

dependencies {
    implementation(project(":core"))

    compileOnly(libs.annotations)
    implementation(libs.bstats.sponge)

    // TODO: More integrations
    compileOnly(libs.bluemap.api)
    compileOnly(libs.squaremap.api)
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
    mergeServiceFiles()

    manifest {
        attributes["Enable-Debug"] = "${hasProperty("enableDebug")}"
    }

    transform(ServiceFileTransformer::class.java) {
        setPath("META-INF/integrations")
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
