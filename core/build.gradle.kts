
repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.annotations)
    implementation(libs.gson)
    implementation(libs.reflections)
    implementation(libs.jtoml.core)
    implementation(libs.bstats.base)
    implementation(libs.caffeine)
    implementation(libs.sqlite)
}
