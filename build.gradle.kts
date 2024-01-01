plugins {
	kotlin("multiplatform") version "1.9.21"
	id("io.kotest.multiplatform") version "5.8.0"
}

group = "moe.micha"
description = "Kotlin/Multiplatform logging library"
version = "0.0.0"

object Versions {
	const val datetime = "0.5.0"
	const val kotest = "5.8.0"
}

repositories {
	mavenCentral()
}

kotlin {
	jvm {
		jvmToolchain(8)

		testRuns.all {
			executionTask.configure {
				useJUnitPlatform()
			}
		}
	}

	js(IR) {
		browser()
		nodejs()
	}

	linuxX64()
	macosX64()
	macosArm64()
	mingwX64()

	sourceSets {
		commonMain.dependencies {
			implementation("org.jetbrains.kotlinx:kotlinx-datetime:${Versions.datetime}")
		}

		commonTest.dependencies {
			implementation("io.kotest:kotest-framework-engine:${Versions.kotest}")
			implementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
		}

		jvmTest.dependencies {
			implementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
		}
	}
}
