plugins {
	kotlin("multiplatform") version "1.9.21"
}

group = "moe.micha"
description = "Kotlin/Multiplatform logging library"
version = "0.0.0"

repositories {
	mavenCentral()
}

kotlin {
	jvm {
		jvmToolchain(8)
	}

	js(IR) {
		browser()
		nodejs()
	}

	linuxX64()
	macosX64()
	macosArm64()
	mingwX64()
}
