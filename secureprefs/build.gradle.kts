import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "am.mino.secureprefs"
    compileSdk = 35

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.mockk)
    testImplementation(libs.junit)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            artifact("${layout.buildDirectory}/outputs/aar/${project.name}-release.aar")

            groupId = "am.mino"
            artifactId = "secureprefs"
            version = "1.0.0"

            pom {
                name.set("SecurePrefs")
                description.set("A secure preferences library for Android.")
                url.set("https://github.com/MxitarHakobyan/SecurePreferences.git")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("mino")
                        name.set("Mkhitar")
                        email.set("hmxo14@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/MxitarHakobyan/SecurePreferences.git")
                    developerConnection.set("scm:git:ssh://git@github.com/MxitarHakobyan/SecurePreferences.git")
                    url.set("https://github.com/MxitarHakobyan/SecurePreferences.git")
                }
            }
        }
    }

    repositories {
        maven {
            name = "SecurePreferences"
            url = uri("https://maven.pkg.github.com/MxitarHakobyan/SecurePreferences.git")

            credentials {
                username = System.getenv("GITHUB_USER")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.withType(Test::class) {
    testLogging {
        events("skipped", "failed", "passed")
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}