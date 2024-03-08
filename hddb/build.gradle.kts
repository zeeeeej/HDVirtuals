plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqlDelight2)
    //id("module.publication")
}

kotlin {
    //targetHierarchy.default()
    jvm()
    androidTarget {
        publishLibraryVariants("release")
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
