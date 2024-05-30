import com.vanniktech.maven.publish.SonatypeHost
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    // id("module.publication")
    alias(libs.plugins.maven.publish)
}

group = "io.github.zeeeeej"
version = "0.0.2"

kotlin {
    targetHierarchy.default()
    jvm()
    androidTarget {
        // publishLibraryVariants("release")
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

//        all {
//            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
//        }

        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(libs.kotlinx.datetime)
                implementation(projects.hdcontext)
                implementation(libs.kotlin.reflect)
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
    namespace = "com.yunext.kmp.common"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
dependencies {
    implementation(libs.firebase.crashlytics.buildtools)
}


mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()

    coordinates("io.github.zeeeeej", "hdcommon", "0.0.2") // 需和注册的namespace对应上

    pom {
        name.set("hdcommon")
        description.set("hdcommon")
        inceptionYear.set("2024")
        url.set("https://github.com/zeeeeej/HDVirtuals")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("zeeeeej.xpl")
                name.set("zeeeeej")
                url.set("https://github.com/zeeeeej/")
            }
        }

        scm {
            url.set("https://github.com/zeeeeej/HDVirtuals")
            connection.set("scm:git:git://gitlab.com/zeeeeej/HDVirtuals.git")
            developerConnection.set("scm:git:ssh://git@gitlab.com:zeeeeej/HDVirtuals.git")
        }
    }
}