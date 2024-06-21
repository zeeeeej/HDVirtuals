plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
            // - compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // - local lib
            implementation(projects.hdcommon)
            implementation(projects.hdcontext)
            implementation(projects.hdresource)
            implementation(projects.hdmqtt)
            implementation(projects.hdmqttVirtuals)
            implementation(projects.hdhttp)
            implementation(projects.hdserial)
            implementation(projects.hddb)
            implementation(projects.hdble)
            implementation(projects.hdui)
            implementation(projects.hdmqttVirtuals)
            implementation(projects.hddomain)

            // voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.core)
            implementation(libs.voyager.bottomSheetNavigator)
            implementation(libs.voyager.tabNavigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.koin)

            api(libs.kotlinx.coroutines.core)
            implementation(libs.napier)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kmp.datetime.picker)

            implementation (libs.zeeeeej.zhongguohong)

        }


        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
//            implementation(libs.voyager.hilt)
            api(libs.kotlinx.coroutines.android)
        }

        iosMain.dependencies {

        }

        jvmMain.dependencies {
            // Note:If you are targeting Desktop, you should provide the dependency org.jetbrains.kotlinx:kotlinx-coroutines-swing, the screenModelScope depends on Dispatchers.Main provided by this library on Desktop. We don't include it because this library is incompatible with IntelliJ Plugin, see. If you are targeting Desktop for IntelliJ plugins, this library does not require to be provied.
            // See:https://voyager.adriel.cafe/screenmodel/coroutines-integration
            api(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "com.yunext.virtuals.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
