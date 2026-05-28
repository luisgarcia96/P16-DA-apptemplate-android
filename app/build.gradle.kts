import java.io.FileInputStream
import java.util.Properties
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("jacoco")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        load(FileInputStream(keystorePropertiesFile))
    }
}

android {
    namespace = "com.openclassrooms.arista"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.openclassrooms.arista"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }

        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    testOptions {
        animationsDisabled = true
    }
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.withType<Test>().configureEach {
    extensions.configure(JacocoTaskExtension::class.java) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

val jacocoExclusions = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/*\$Companion*.*",
    "**/*\$Lambda$*.*",
    "**/*\$inlined$*.*",
    "**/*_Factory*.*",
    "**/*_MembersInjector*.*",
)

val debugTree = fileTree("$buildDir/tmp/kotlin-classes/debug") {
    exclude(jacocoExclusions)
}
val javacDebugTree = fileTree("$buildDir/intermediates/javac/debug/classes") {
    exclude(jacocoExclusions)
}

val mainSource = files(
    "$projectDir/src/main/java",
    "$projectDir/src/main/kotlin"
)

val coverageExecutionData = fileTree(buildDir) {
    include(
        "jacoco/testDebugUnitTest.exec",
        "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
        "outputs/code_coverage/debugAndroidTest/connected/**/*.ec",
        "outputs/code-coverage/connected/*coverage.ec"
    )
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Generates JaCoCo coverage reports for unit and instrumentation tests."
    dependsOn("testDebugUnitTest", "connectedDebugAndroidTest")

    classDirectories.setFrom(files(debugTree, javacDebugTree))
    sourceDirectories.setFrom(mainSource)
    executionData.setFrom(coverageExecutionData)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    group = "verification"
    description = "Verifies JaCoCo coverage metrics for unit and instrumentation tests."
    dependsOn("testDebugUnitTest", "connectedDebugAndroidTest")

    classDirectories.setFrom(files(debugTree, javacDebugTree))
    sourceDirectories.setFrom(mainSource)
    executionData.setFrom(coverageExecutionData)

    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.60".toBigDecimal()
            }
        }
    }
}

tasks.named("check") {
    dependsOn("jacocoTestCoverageVerification")
}

dependencies {
    val roomVersion = "2.6.0"

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    //Hilt
    implementation("com.google.dagger:hilt-android:2.42")
    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:2.42")

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    //Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0")

    androidTestImplementation("androidx.test:runner:1.6.0")
    androidTestImplementation("androidx.test:rules:1.6.0")
    implementation("androidx.tracing:tracing:1.2.0")
}
kapt {
    correctErrorTypes = true
}
