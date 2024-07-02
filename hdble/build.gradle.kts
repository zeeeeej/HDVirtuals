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
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain.dependencies {


            api(libs.kotlinx.coroutines.android)

            implementation(libs.androidx.bluetooth)
            api(libs.fastble)
            implementation(libs.androidx.appcompat)

        }

        jvmMain.dependencies {

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
    }
}

android {
    namespace = "com.yunext.kmp.ble"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
