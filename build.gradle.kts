plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm") version "2.1.20-Beta2"
	id("org.jetbrains.intellij.platform") version "2.2.1"
}

group = "tr.xyz"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
	intellijPlatform {
		defaultRepositories()
	}
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellijPlatform {
	pluginConfiguration {
		ideaVersion {
			sinceBuild = "242"
			untilBuild = "999.*"
		}
		
		changeNotes = """
      Initial version
    """.trimIndent()
	}
}

dependencies {
	intellijPlatform {
		create("IC", "2024.2.5")
		testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
		
		// Add necessary plugin dependencies for compilation here, example:
		// bundledPlugin("com.intellij.java")
	}
}

tasks {
	// Set the JVM compatibility versions
	withType<JavaCompile> {
		sourceCompatibility = "21"
		targetCompatibility = "21"
	}
	withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions.jvmTarget = "21"
	}
}