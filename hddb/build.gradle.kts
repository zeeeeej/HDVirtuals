import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqlDelight2)
    //id("module.publication")
//    alias(libs.plugins.compose.compiler)
//    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    //targetHierarchy.default()
    jvm()
    androidTarget() {
//        @OptIn(ExperimentalKotlinGradlePluginApi::class)
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_11)
//        }

        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
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
                implementation(libs.sqlite.stately.common)
                implementation(libs.sqlite.stately.concurrency)

                api(libs.sqlDelight2.runtime)
                api(libs.sqlDelight2.adapter)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain.dependencies {
            api(libs.sqlDelight2.driver.android)
        }

        iosMain.dependencies {
            api(libs.sqlDelight2.driver.native)
        }

        jvmMain.dependencies {
            api(libs.sqlDelight2.driver.sqlite)
        }
    }
}

android {
    namespace = "com.yunext.kmp.db"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

sqldelight {
    databases {
        create("DemoDatabase") {
            packageName.set("com.yunext.kmp.database")
            //dialect("app.cash.sqldelight:sqlite-3-24-dialect:2.0.1")
        }
        //linkSqlite = true

    }

}
