import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.getByType

val Project.globalExtra
    get() = this.rootProject.extensions.getByType(ExtraPropertiesExtension::class)

fun Project.getGlobalProperty(key: String): String? {
    return this.globalExtra.get(key)?.toString()
}

fun Project.getGlobalPropertyAssert(key: String): String {
    return this.getGlobalProperty(key) ?: throw AssertionError("Global property $key not set")
}

fun Project.setGlobalProperty(key: String, value: String?) {
    this.globalExtra.set(key, value)
}

// START Helpers

object KnownGlobalProperties {
    const val DEBUG_MODE: String = "debugMode"
    const val JAVA_VERSION: String = "javaVersion"
    const val ANNOTATIONS_VERSION: String = "annotationsVersion"
    const val MC_VERSION: String = "mcVersion"
    const val SERVER_VERSION: String = "serverVersion"
    const val STATS_VERSION: String = "bStatsVersion"
}

var Project.debugMode: Boolean
    get() = this.getGlobalProperty(KnownGlobalProperties.DEBUG_MODE).toBoolean()
    set(value) = this.setGlobalProperty(KnownGlobalProperties.DEBUG_MODE, value.toString())

var Project.javaVersion: Int
    get() = this.getGlobalPropertyAssert(KnownGlobalProperties.JAVA_VERSION).toInt()
    set(value) = this.setGlobalProperty(KnownGlobalProperties.JAVA_VERSION, value.toString())

var Project.annotationsVersion
    get() = this.getGlobalPropertyAssert(KnownGlobalProperties.ANNOTATIONS_VERSION)
    set(value) = this.setGlobalProperty(KnownGlobalProperties.ANNOTATIONS_VERSION, value)

var Project.mcVersion
    get() = this.getGlobalPropertyAssert(KnownGlobalProperties.MC_VERSION)
    set(value) = this.setGlobalProperty(KnownGlobalProperties.MC_VERSION, value)

var Project.serverVersion
    get() = this.getGlobalPropertyAssert(KnownGlobalProperties.SERVER_VERSION)
    set(value) = this.setGlobalProperty(KnownGlobalProperties.SERVER_VERSION, value)

var Project.statsVersion
    get() = this.getGlobalPropertyAssert(KnownGlobalProperties.STATS_VERSION)
    set(value) = this.setGlobalProperty(KnownGlobalProperties.STATS_VERSION, value)