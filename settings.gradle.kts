rootProject.name = "HDVirtuals"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":composeApp")
include(":server")
include(":shared")
include(":hdcommon")
include(":hdresource")
include(":hdresource-ksp")
include(":hddb")
include(":hdmqtt")
include(":hdmqtt-virtuals")
include(":hdcontext")
include(":hdhttp")
include(":hdble")
include(":hdserial")
include(":hdui")
include(":hddomain")