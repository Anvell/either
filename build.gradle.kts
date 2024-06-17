@file:Suppress("DSL_SCOPE_VIOLATION", "SpellCheckingInspection")

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly
import java.util.Base64

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.dependency.updates)
    id("maven-publish")
    id("signing")
}

val kotlinterId = libs.plugins.kotlinter.get().pluginId

subprojects {
    apply {
        plugin(kotlinterId)
        plugin("maven-publish")
        plugin("signing")
    }

    signing {
        val secretKey = Base64
            .getDecoder()
            .decode(properties["anvell_signing_gnupg_key"].toString())
            .toString(Charsets.UTF_8)

        useInMemoryPgpKeys(
            secretKey,
            properties["anvell_signing_gnupg_pass"].toString()
        )
        sign(publishing.publications)
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        val version = candidate.version.toLowerCaseAsciiOnly()

        listOf("-alpha", "-beta", "-rc")
            .any { it in version }
    }
}
