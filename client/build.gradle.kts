import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose") version "1.0.1"
}

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

version = "0.3.0"

dependencies {
    implementation(project(":shared"))

    val ktorVersion = "1.6.7"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")

    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)

    implementation("net.java.dev.jna:jna:5.9.0")
    implementation("net.java.dev.jna:jna-platform:5.9.0")

    testImplementation("io.ktor:ktor-client-tests:$ktorVersion")
}

compose.desktop {
    application {
        mainClass = "${group}.downlomatic.client.LauncherKt"

        nativeDistributions {
            javaHome = System.getenv("JPACKAGE_HOME")

            packageName = "Downlomatic"
            packageVersion = version.toString()
            description = "Automatic Multimedia Download Tool"
            copyright = "© 2022 Tim Peko"
            vendor = "TimerErTim"
            licenseFile.set(rootProject.file("LICENSE"))

            modules("java.instrument", "jdk.unsupported")

            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Rpm,
                TargetFormat.Msi,
                TargetFormat.Exe,
                //TargetFormat.Pkg
            )

            linux {
                debMaintainer = "timerertim@gmail.com"
                appCategory = "misc;utils"
                menuGroup = "Utility;Java"
                appRelease = "0"
                rpmLicenseType = "AGPL-3.0 License"
            }

            macOS {
                packageVersion = this@nativeDistributions.packageVersion?.removePrefix("0.")

                bundleID = "eu.timerertim.downlomatic"
            }

            windows {
                console = false
                upgradeUuid = "F87E2223-5DE4-47B8-9ED1-51CC8FB9A71C"
            }
        }
    }
}
