import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm") version "2.2.21"
	id("org.jetbrains.intellij.platform") version "2.10.4"
}

group = "com.mrtarantas"
version = "1.0.1"

val targetBuild = "252"
val targetIJVersion = "2025.2.1"

sourceSets {
	main {
		java.srcDir("src/main/gen")
	}
}

repositories {
	mavenCentral()
	intellijPlatform {
		defaultRepositories()
	}
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
	intellijPlatform {
		create("IC", targetIJVersion)
		testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
	}
}

intellijPlatform {
	pluginConfiguration {
		ideaVersion {
			sinceBuild = targetBuild
			untilBuild = provider { null }
		}

		changeNotes = """
      Initial version
    """.trimIndent()
	}
}

dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
	// skiko transitively pulls in `org.jetbrains.runtime:jbr-api`, which bundles the
	// `com.jetbrains.*` JBR API classes (JBR, JBRFileDialog, SharedTextures, ...).
	// Those classes are already provided by the JetBrains Runtime. If the plugin bundles
	// its own copy, the plugin classloader shadows the platform one and the JBR API's
	// internal ProxyRepository fails with "Incompatible classloader", breaking JBR
	// services IDE-wide (native file chooser, shared textures, etc.). So exclude it —
	// the real implementation comes from the running JBR at runtime.
	implementation("org.jetbrains.skiko:skiko-awt:0.9.22") {
		exclude(group = "org.jetbrains.runtime", module = "jbr-api")
	}
	runtimeOnly("org.jetbrains.skiko:skiko-awt-runtime-all:0.9.22") {
		exclude(group = "org.jetbrains.runtime", module = "jbr-api")
	}
}

tasks {
	withType<JavaCompile> {
		sourceCompatibility = "21"
		targetCompatibility = "21"
	}
	withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_21)
		}
	}
	patchPluginXml {
		sinceBuild.set(targetBuild)
	}
}