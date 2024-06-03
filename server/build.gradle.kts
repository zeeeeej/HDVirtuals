plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
//    alias(libs.plugins.compose.compiler)
//    alias(libs.plugins.jetbrainsCompose)
}

group = "com.yunext.virtuals"
version = "1.0.0"
application {
    mainClass.set("com.yunext.virtuals.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}