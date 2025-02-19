plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm") version "2.1.20-Beta2"
	id("org.jetbrains.intellij") version "1.17.4"
}

group = "tr.xyz"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
	version.set("2023.3.1")
	type.set("IC") // Target IDE Platform
	
	plugins.set(listOf())
}

dependencies { // Burayı ekle
	implementation("org.jetbrains:annotations:24.0.1") // veya güncel bir versiyon
}

tasks {
	// Set the JVM compatibility versions
	withType<JavaCompile> {
		sourceCompatibility = "17"
		targetCompatibility = "17"
	}
	withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions.jvmTarget = "17"
	}
	
	patchPluginXml {
		sinceBuild.set("233")
		untilBuild.set("991.*")
	}
	
	signPlugin {
		certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
		privateKey.set(System.getenv("PRIVATE_KEY"))
		password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
	}
	
	publishPlugin {
		token.set(System.getenv("PUBLISH_TOKEN"))
	}
}