import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

plugins {
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":bukkit"))
    compileOnly(libs.annotations)
    compileOnly(libs.spigot.api)

    // Adventure
    implementation(libs.adventure.api)
    implementation(libs.adventure.platform.bukkit)
    implementation(libs.adventure.minimessage)
    implementation(libs.adventure.plain)
}

tasks.shadowJar {
    archiveClassifier.set("")

    manifest {
        attributes["Enable-Debug"] = "${hasProperty("enableDebug")}"
    }

    transform(ServiceFileTransformer::class.java) {
        setPath("META-INF/integrations")
    }

    // Library relocations
    val libPkg = "io.github.wasabithumb.xclaim.shadow"
    relocate("io.github.wasabithumb.jtoml", "${libPkg}.jtoml")
    relocate("org.reflections", "${libPkg}.reflections")
    relocate("org.bstats", "${libPkg}.bstats")
    relocate("com.github.benmanes.caffeine", "${libPkg}.caffeine")
    relocate("org.sqlite", "${libPkg}.sqlite")
    relocate("org.bstats", "${libPkg}.bstats")
    relocate("net.kyori.adventure", "${libPkg}.adventure")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
