import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

plugins {
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.0"
    id("io.github.goooler.shadow") version "8.1.8"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":bukkit"))
    implementation(project(":paper:extras"))
    compileOnly(libs.annotations)
    compileOnly(libs.paper.api)
}

tasks.shadowJar {
    archiveClassifier.set("")

    manifest {
        attributes["Enable-Debug"] = "${hasProperty("enableDebug")}"
    }

    transform(ServiceFileTransformer::class.java) {
        setPath("META-INF/integrations")
    }

    // Library exclusions (present in Paper)
    dependencies {
        exclude(dependency("com.google.code.gson:gson"))
        exclude(dependency("com.google.errorprone:error_prone_annotations"))
        exclude(dependency("commons-lang:commons-lang"))
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

paperPluginYaml {
    main = "io.github.wasabithumb.xclaim.XClaimPlugin"
    bootstrapper = "io.github.wasabithumb.xclaim.XClaimPluginBootstrap"
    description = "A fully-featured chunk claiming system for community servers"
    apiVersion = "1.19"
    website = "https://wasabithumb.github.io/"
    foliaSupported = true
    prefix = "XC"
    authors = listOf("WasabiThumbs")
    permissions {
        register("xclaim.admin") {
            description = "Allows you to modify/delete any claim"
            default = Permission.Default.OP
        }
    }
    dependencies.server {
        register("PlaceholderAPI") {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        }
        register("dynmap") {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        }
        register("Essentials") {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        }
        register("EssentialsX") {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        }
        register("Vault") {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        }
        register("BlueMap") {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        }
        register("WorldGuard") {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        }
    }
}
