import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

version = "0.1.0"

dependencies {
    implementation(project(":shared"))

    val ktorVersion = "1.6.6"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation(compose.desktop.currentOs)

    implementation("net.java.dev.jna:jna:5.9.0")
    implementation("net.java.dev.jna:jna-platform:5.9.0")
}

compose.desktop {
    application {
        mainClass = "${group}.downlomatic.client.LauncherKt"

        nativeDistributions {
            javaHome = System.getenv("JPACKAGE_HOME")

            packageName = "Downlomatic"
            packageVersion = version.toString()
            description = "Automatic Multimedia Download Tool"
            copyright = "© 2021 Tim Peko"
            vendor = "TimerErTim"
            licenseFile.set(rootProject.file("LICENSE"))

            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Rpm,
                TargetFormat.Msi,
                TargetFormat.Exe
            )

            macOS {
                packageVersion = this@nativeDistributions.packageVersion?.removePrefix("0.")


            }
        }
    }
}