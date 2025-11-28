plugins {
   kotlin("jvm")
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

repositories {
   mavenCentral()
}

dependencies {
   implementation(kotlin("stdlib"))
   
   // Testing
   testImplementation(kotlin("test"))
   testImplementation(kotlin("test-junit5"))
}

kotlin {
   jvmToolchain(21)
}

tasks {
   test {
      useJUnitPlatform()
   }
}

