plugins {
   alias(libs.plugins.kotlin) apply false
   alias(libs.plugins.intelliJPlatform) apply false
   alias(libs.plugins.changelog) apply false
   alias(libs.plugins.qodana) apply false
   alias(libs.plugins.kover) apply false
   alias(libs.plugins.ktlint) apply false
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

tasks {
   wrapper {
      gradleVersion = providers.gradleProperty("gradleVersion").get()
   }
}
