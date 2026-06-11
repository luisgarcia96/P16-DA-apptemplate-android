// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
    id("com.google.dagger.hilt.android") version "2.42" apply false
    id("org.sonarqube") version "5.1.0.4882"
    id("com.google.firebase.appdistribution") version "5.2.1" apply false
}

allprojects {
    dependencyLocking {
        lockAllConfigurations()
    }
}

fun ProviderFactory.sonarValue(propertyName: String, environmentName: String) =
    gradleProperty(propertyName)
        .orElse(environmentVariable(environmentName))
        .orNull

sonar {
    properties {
        val sonarProjectKey = providers.sonarValue("sonar.projectKey", "SONAR_PROJECT_KEY")
        val sonarOrganization = providers.sonarValue("sonar.organization", "SONAR_ORGANIZATION")
        val sonarHostUrl = providers.sonarValue("sonar.host.url", "SONAR_HOST_URL")
            ?: "https://sonarcloud.io"

        sonarProjectKey?.let { property("sonar.projectKey", it) }
        sonarOrganization?.let { property("sonar.organization", it) }
        property("sonar.host.url", sonarHostUrl)
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"
        )
    }
}

tasks.named("sonar") {
    dependsOn(":app:jacocoTestReport")
}
