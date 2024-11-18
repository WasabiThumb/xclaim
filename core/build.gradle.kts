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
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveClassifier.set("")

    val libPkg = "io.github.wasabithumb.xclaim.shadow"
    relocate("com.moandjiezana.toml", "${libPkg}.toml")
    relocate("org.reflections", "${libPkg}.reflections")
    relocate("org.bstats", "${libPkg}.bstats")
}

artifacts {
    add("shadow", tasks.shadowJar)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
