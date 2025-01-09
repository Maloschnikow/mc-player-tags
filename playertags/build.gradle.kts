/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/8.0.2/userguide/building_java_projects.html
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.12"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

repositories {
    // Use Maven Central for resolving dependencies.
    //mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    // Use JUnit Jupiter for testing.
    //testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")

    // This dependency is used by the application.
    //implementation("com.google.guava:guava:31.1-jre")

    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    //compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
}

application {
    // Define the main class for the application.
    mainClass.set("io.maloschnikow.playertags.PlayerTags")
}

/* tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
} */

//is needed according to documentation, but everything works fine without it
/* java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
} */

tasks.jar {
    manifest {
      attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

tasks {
    runServer {
      // Configure the Minecraft version for our task.
      // This is the only required configuration besides applying the plugin.
      // Your plugin's jar (or shadowJar if present) will be used automatically.
      minecraftVersion("1.21.4")
    }
  }

//According to documentation this should be here, but it throws an error
//paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
