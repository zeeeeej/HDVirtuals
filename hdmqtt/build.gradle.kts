import com.android.build.gradle.internal.ide.kmp.KotlinAndroidSourceSetMarker.Companion.android
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    //id("module.publication")
    alias(libs.plugins.kotlinSerialization)
//    alias(libs.plugins.compose.compiler)
//    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    targetHierarchy.default()
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
                implementation(projects.hdcommon)

                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.kotlinx.serialization.json)
                // KMQTT
                implementation(libs.kmqtt.common)
                implementation(libs.kmqtt.client)

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain.dependencies {
            implementation(libs.mqtt3.client)
            implementation(libs.mqtt3.android.service)
            implementation(libs.androidx.legacy.support.v4)
            // Fix: java.lang.NoClassDefFoundError: Failed resolution of: Landroid/support/v4/content/LocalBroadcastManager;
            // android.enableJetifier=true
            implementation(libs.android.support.v4)

            api(libs.kotlinx.coroutines.android)

        }

        jvmMain.dependencies {
            implementation(libs.mqtt3.client)
            implementation(libs.mqtt3.jvm.jdk15on)

            api(libs.kotlinx.coroutines.swing)

        }

        val iosMain by getting {
            dependsOn(commonMain)

        }

        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
            dependencies {
                implementation(files("./nativeInterop/openssl-ios-simulator-arm64.klib"))
            }
        }
        // TODO 旧的方式
//        iosMain.dependencies {
//            dependencies {
//                implementation(files("src/nativeInterop/openssl-ios-simulator-arm64.klib"))
//            }
//        }


    }
}

android {
    namespace = "com.yunext.kmp.mqtt"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
