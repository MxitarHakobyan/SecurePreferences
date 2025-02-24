import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.nmcp)
    id("signing")
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
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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

mavenPublishing {
    signAllPublications()
}

// Function to load properties from local.properties
fun loadLocalProperties(): Properties {
    val properties = Properties()
    val file = File(rootProject.projectDir, "local.properties")
    if (file.exists()) {
        FileInputStream(file).use { properties.load(it) }
    }
    return properties
}

val localProps = loadLocalProperties()


val signingKeyId: String? = localProps.getProperty("signing.keyId")
val signingPassword: String? = localProps.getProperty("signing.password")
val signingKeyRingFile: String? = localProps.getProperty("signing.secretKeyRingFile")

signing {
    useGpgCmd()
    useInMemoryPgpKeys(
        localProps.getProperty("SIGNING_KEY") ?: System.getenv("SIGNING_KEY"),
        localProps.getProperty("SIGNING_PASSWORD") ?: System.getenv("SIGNING_PASSWORD")
    )
    sign(publishing.publications)
}

nmcp {
    publishAllPublications {
        val keyUsername = "SONATYPE_USERNAME"
        val keyPassword = "SONATYPE_PASSWORD"
        username = localProps.getProperty(keyUsername)?.toString() ?: System.getenv(keyUsername)
        password = localProps.getProperty(keyPassword)?.toString() ?: System.getenv(keyPassword)

        publicationType = "USER_MANAGED"
    }
}