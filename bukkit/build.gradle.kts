plugins {
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
    maven("https://jitpack.io")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.bluecolored.de/releases/")
    maven("https://repo.mikeprimm.com/")
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    implementation(project(":core"))

    compileOnly("org.jetbrains:annotations:${annotationsVersion}")
    implementation("org.bstats:bstats-bukkit:${statsVersion}")
    compileOnly("org.spigotmc:spigot-api:${serverVersion}")

    // Vault integration
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    // Essentials integration
    compileOnly("net.essentialsx:EssentialsX:2.20.1")

    // WorldGuard integration
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")

    // BlueMap integration
    compileOnly("de.bluecolored:bluemap-api:2.7.3")

    // Dynmap integration
    compileOnly("us.dynmap:DynmapCoreAPI:3.6")
    compileOnly("us.dynmap:dynmap-api:3.6")

    // Placeholder integration
    compileOnly("me.clip:placeholderapi:2.11.6")
}
