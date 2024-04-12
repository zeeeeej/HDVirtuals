import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    targetHierarchy.default()
    jvm()
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
//        it.binaries.executable(
//            "hdcontextexe",
//            listOf(NativeBuildType.DEBUG, NativeBuildType.RELEASE)
//        ) {
//
//        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                implementation(libs.kotlinx.serialization.json)

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain.dependencies {
            implementation(libs.android.lifecycle.service)
            implementation(libs.android.lifecycle.common.java8)
            implementation(libs.android.lifecycle.process)
            implementation(libs.android.lifecycle.compiler)
            implementation(libs.android.lifecycle.runtime.ktx)
            implementation(libs.android.lifecycle.livedata.ktx)
            implementation(libs.android.lifecycle.viewmodel.ktx)
            implementation(libs.android.lifecycle.viewmodel.savedstate)
        }
    }
}

android {
    namespace = "com.yunext.kmp.context"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
