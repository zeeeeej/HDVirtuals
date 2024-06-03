import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    //id("module.publication")
//    alias(libs.plugins.compose.compiler)
//    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
//    targetHierarchy.default()
    jvm()
    androidTarget {
        //publishLibraryVariants("release")
        compilations.all {
//            kotlinOptions {
//                jvmTarget = "1.8"
//            }
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(projects.hdcontext)

                api(libs.kotlinx.datetime)
                api(libs.kotlinx.coroutines.core)
                api(libs.ktor.client.core)
                api(libs.ktor.client.cio)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.serialization.kotlinx.json)
                api(libs.ktor.serialization.kotlinx.protobuf)
                implementation(libs.ktor.client.logging)
                implementation(libs.napier)

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain.dependencies {
            api(libs.kotlinx.coroutines.android)
            api(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            api(libs.ktor.client.darwin)
        }

        jvmMain.dependencies{
            api(libs.ktor.client.okhttp)
        }
    }
}

android {
    namespace = "com.yunext.kmp.http"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
