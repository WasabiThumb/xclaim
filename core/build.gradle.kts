
repositories {
    mavenCentral()
    maven("https://repo.bluecolored.de/releases/")
}

dependencies {
    compileOnly(libs.annotations)
    implementation(libs.gson)
    implementation(libs.reflections)
    implementation(libs.jtoml.core)
    implementation(libs.bstats.base)
    implementation(libs.caffeine)
    implementation(libs.sqlite)

    // Integrations
    compileOnly(libs.bluemap.api)
    compileOnly(libs.squaremap.api)
}
