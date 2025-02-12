plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.nmcp)
}

android {
    namespace = "am.mino.secureprefs"
    compileSdk = 35
    version = findProperty("VERSION_NAME") as String

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

nmcp {
    publishAllPublications {
        val keyUsername = "SONATYPE_USERNAME"
        val keyPassword = "SONATYPE_PASSWORD"
        username = findProperty(keyUsername)?.toString() ?: System.getenv(keyUsername)
        password = findProperty(keyPassword)?.toString() ?: System.getenv(keyPassword)

        publicationType = "USER_MANAGED"
    }
}