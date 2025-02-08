import org.jetbrains.dokka.gradle.DokkaTask

plugins {
	kotlin("multiplatform") version "2.1.10"
	id("io.kotest.multiplatform") version "6.0.0.M1"
	id("org.jetbrains.dokka") version "2.0.0"

	id("maven-publish")
	id("signing")
}

group = "moe.micha"
description = "Kotlin/Multiplatform logging library"
version = "0.4.1"

object Versions {
	const val datetime = "0.6.1"
	const val kotest = "6.0.0.M1"
	const val okio = "3.10.2"
}

repositories {
	mavenCentral()
}

kotlin {
	jvm {

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
			implementation("com.squareup.okio:okio:${Versions.okio}")
		}

		commonTest.dependencies {
			implementation("io.kotest:kotest-framework-engine:${Versions.kotest}")
			implementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
		}

		jvmTest.dependencies {
			implementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
		}
	}

	jvmToolchain(8)
}

// allow rerunning tests with no changes (prevents the Test Events Were Not Received message):
tasks.withType<Test> {
	outputs.upToDateWhen { false }
}

val sonatypeUsername: String? = System.getenv("SONATYPE_USERNAME")
val sonatypePassword: String? = System.getenv("SONATYPE_PASSWORD")
val gpgPrivateKey: String? = System.getenv("GPG_PRIVATE_KEY")
val gpgKeyPassword: String? = System.getenv("GPG_KEY_PASSWORD")

val repositoryBranch: String? = System.getenv("GITHUB_REF_NAME")

val repositoryUrl: String? = run {
	val githubServerUrl: String? = System.getenv("GITHUB_SERVER_URL")
	val githubRepository: String? = System.getenv("GITHUB_REPOSITORY")

	if (githubServerUrl == null || githubRepository == null) {
		null
	} else {
		"$githubServerUrl/$githubRepository"
	}
}

val dokkaOutputDir = layout.buildDirectory.get().dir("dokka").dir("html")

tasks.withType<DokkaTask>().configureEach {
	outputDirectory = dokkaOutputDir

	dokkaSourceSets.configureEach {
		sourceLink {
			localDirectory = projectDir.resolve("src")
			remoteUrl = uri("$repositoryUrl/tree/$repositoryBranch/src").toURL()
			remoteLineSuffix = "#L"
		}
	}
}

val javadocJar = tasks.register<Jar>("javadocJar") {
	archiveClassifier = "javadoc"

	from(dokkaOutputDir)
}

tasks.withType<AbstractPublishToMaven> {
	dependsOn(tasks.withType<Sign>())
}

publishing {
	repositories {
		maven {
			name = "oss"
			val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
			val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")

			url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

			credentials {
				username = sonatypeUsername
				password = sonatypePassword
			}
		}
	}

	publications.withType<MavenPublication> {
		artifact(javadocJar)

		pom {
			name = project.name
			description = project.description
			url = repositoryUrl

			licenses {
				license {
					name = "MIT"
					url = "$repositoryUrl/blob/$repositoryBranch/license.md"
				}
			}

			issueManagement {
				system = "GitHub"
				url = "$repositoryUrl/issues"
			}

			scm {
				connection = "$repositoryUrl.git"
				url = repositoryUrl
			}

			developers {
				developer {
					name = "Micha Lehmann"
					email = "michalehmann0112@gmail.com"
				}
			}
		}
	}
}

signing {
	setRequired { // avoid having to sign local publications
		!gradle.taskGraph.hasTask(":publishToMavenLocal")
	}

	useInMemoryPgpKeys(
		gpgPrivateKey,
		gpgKeyPassword,
	)

	sign(publishing.publications)
}
