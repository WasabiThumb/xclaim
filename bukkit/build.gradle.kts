import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    alias(libs.plugins.resource.factory.bukkit)
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.bluecolored.de/releases/")
    maven("https://repo.mikeprimm.com/")
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    implementation(project(":core"))
    compileOnly(libs.annotations)
    compileOnly(libs.spigot.api)
    implementation(libs.bstats.bukkit)

    // Integrations
    compileOnly(libs.vault.api)
    compileOnly(libs.essentials)
    compileOnly(libs.worldguard.bukkit)
    compileOnly(libs.bluemap.api)
    compileOnly(libs.dynmap.core)
    compileOnly(libs.dynmap.api)
    compileOnly(libs.squaremap.api)
    compileOnly(libs.papi)
}

bukkitPluginYaml {
    name = "XClaim"
    main = "io.github.wasabithumb.xclaim.XClaimPlugin"
    description = "A fully-featured chunk claiming system for community servers"
    apiVersion = "1.18"
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
    softDepend = listOf(
        "dynmap",
        "squaremap",
        "Essentials",
        "EssentialsX",
        "Vault",
        "BlueMap",
        "WorldGuard",
        "PlaceholderAPI"
    )
    commands {
        register("xclaim") {
            description = "XClaim main command"
            aliases = listOf("xc")
        }
    }
}
