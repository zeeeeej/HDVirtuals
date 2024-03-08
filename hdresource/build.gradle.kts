plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    //id("module.publication")
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
                // lib modules
                implementation(compose.runtime)
                implementation(compose.foundation)
                //implementation(compose.material)
                //implementation(compose.ui)
                //implementation(compose.components.resources)
                //implementation(compose.components.uiToolingPreview)

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "com.yunext.kmp.source"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
