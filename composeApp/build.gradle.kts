import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
//    alias(libs.plugins.ksp)
//    alias(libs.plugins.cocoapods)
    alias(libs.plugins.compose.compiler)
}

kotlin {

    androidTarget {
//        compilations.all {
//            kotlinOptions {
//                jvmTarget = "11"
//            }
//        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }


    }

    jvm("desktop"){

    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        // fix ios can't link sqlite https://github.com/cashapp/sqldelight/issues/1442
        with(iosTarget) {
            binaries.framework {
                baseName = "ComposeApp"
                isStatic = false
                export(project(":shared"))
                export(project(":hdcommon"))
                export(project(":hdmqtt"))
                // Export transitively.
                // transitiveExport = true
            }
            compilations.all {
                kotlinOptions.freeCompilerArgs += arrayOf("-linker-options", "-lsqlite3")
            }

            //            val result = binaries.getByName("hdcontextexeDebugExecutable")
//            println(
//                """
//                ^^^^^^^^^^^^
//                result = $result
//
//            """.trimIndent()
//            )

//            binaries.sharedLib {
//                export(project(":shared"))
//            }

//            binaries.staticLib {
//                export(project(":shared"))
//            }
        }
    }

    sourceSets {
        val desktopMain by getting

        iosMain.dependencies {
            api(project(":shared"))
            api(project(":hdcommon"))
            implementation(project(":hdmqtt"))
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.mqtt3.client)
            implementation(libs.mqtt3.android.service)
            implementation(libs.androidx.legacy.support.v4)
            implementation(libs.kotlinx.coroutines.android)

            implementation (libs.accompanist.systemuicontroller)




        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(projects.shared)
            //
            implementation(projects.hdcontext)
            implementation(projects.hdcommon)
            implementation(projects.hdresource)

            implementation(libs.napier)
            implementation(libs.kotlinx.coroutines.core)

        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}



android {
    namespace = "com.yunext.virtuals"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.yunext.virtuals"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        // https://developer.android.com/studio/write/java8-support#library-desugaring
        // isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
        // https://developer.android.com/studio/write/java8-support#library-desugaring
        // coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.yunext.virtuals"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
//    ksp(projects.hdresourceKsp)
    implementation(projects.hdresource)
}


composeCompiler {
    enableStrongSkippingMode = true

//    reportsDestination = layout.buildDirectory.dir("compose_compiler")
//    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}