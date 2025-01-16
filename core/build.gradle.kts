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
}

dependencies {
    compileOnly("org.jetbrains:annotations:${annotationsVersion}")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("org.bstats:bstats-base:${statsVersion}")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
}
