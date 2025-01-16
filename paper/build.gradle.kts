import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

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
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":bukkit"))
    implementation(project(":paper:bootstrap"))
    compileOnly("org.jetbrains:annotations:${annotationsVersion}")
    compileOnly("io.papermc.paper:paper-api:${serverVersion}")
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveClassifier.set("")

    manifest {
        attributes["Enable-Debug"] = if (debugMode) "true" else "false"
    }

    transform(ServiceFileTransformer::class.java) {
        setPath("META-INF/integrations")
    }

    // Library exclusions (present in Paper)
    dependencies {
        exclude(dependency("com.google.code.gson:gson"))
        exclude(dependency("org.slf4j:slf4j-api"))
    }

    // Library relocations
    val libPkg = "io.github.wasabithumb.xclaim.shadow"
    relocate("com.moandjiezana.toml", "${libPkg}.toml")
    relocate("org.reflections", "${libPkg}.reflections")
    relocate("org.bstats", "${libPkg}.bstats")
    relocate("com.github.benmanes.caffeine", "${libPkg}.caffeine")
    relocate("org.sqlite", "${libPkg}.sqlite")
    relocate("org.bstats", "${libPkg}.bstats")
}

artifacts {
    add("default", tasks.shadowJar)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
