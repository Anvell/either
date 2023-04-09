@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotest.multiplatform)
    alias(libs.plugins.dokka)
    id("maven-publish")
}

kotlin {
    explicitApi()

    jvm()
    js(IR) {
        browser()
    }
    iosArm32()
    iosArm64()
    iosX64()
    linuxX64()
    macosX64()
    macosArm64()
    mingwX64()
    tvosArm64()
    tvosX64()
    watchosArm32()
    watchosArm64()
    watchosX86()
    watchosX64()

    sourceSets {
        all {
            with(languageSettings) {
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.experimental.ExperimentalTypeInference")
            }
        }

        getByName("commonMain") {
            dependencies {
                implementation(libs.coroutines.core)
            }
        }
        getByName("commonTest") {
            dependencies {
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
            }
        }
        getByName("jvmTest") {
            dependencies {
                runtimeOnly(libs.kotest.runner.junit5)
            }
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}
