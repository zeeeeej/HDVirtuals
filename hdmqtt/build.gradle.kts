import com.android.build.gradle.internal.ide.kmp.KotlinAndroidSourceSetMarker.Companion.android

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    //id("module.publication")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    targetHierarchy.default()
    jvm()
    androidTarget {
        //publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
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
        iosMain.dependencies {

        }
    }
}

android {
    namespace = "com.yunext.kmp.mqtt"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
