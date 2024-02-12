[![Kotlin 1.9.21](https://img.shields.io/badge/Kotlin%2FMultiplatform-1.9.21-7F52FF.svg?logo=kotlin)](http://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/moe.micha/logbook?label=Latest%20Version)](https://central.sonatype.com/artifact/moe.micha/logbook)
[![CI status badge](https://img.shields.io/github/actions/workflow/status/Micha-ohne-el/logbook/integration.yaml?label=CI)](https://github.com/Micha-ohne-el/logbook/actions/workflows/integration.yaml)
[![CD status badge](https://img.shields.io/github/actions/workflow/status/Micha-ohne-el/logbook/deployment.yaml?label=CD)](https://github.com/Micha-ohne-el/logbook/actions/workflows/deployment.yaml)
[![Documentation available](https://img.shields.io/badge/Documentation-available-blue)](https://logbook.micha.moe)
[![License – MIT](https://img.shields.io/github/license/Micha-ohne-el/logbook?color=gold&label=License)](https://github.com/Micha-ohne-el/logbook/blob/main/license.md)

# logbook

Kotlin/Multiplatform logging library.

## Installation

Simply add a dependency on logbook to your Gradle project:

```kt
repositories {
	mavenCentral()
}

// for Kotlin/Multiplatform projects:
kotlin {
	sourceSets {
		commonMain.configure {
			dependencies {
				implementation("moe.micha:logbook:$version")
			}
		}
	}
}

// for Kotlin/JVM projects:
kotlin {
	dependencies {
		implementation("moe.micha:logbook:$version")
	}
}
```

Replace `$version` with the version you want, a list of which can be retrieved on
[Maven Central](https://central.sonatype.com/artifact/moe.micha/logbook/versions).

## Contributing

I would love for people to contribute to logbook!

If you feel like the library is missing something or you've encountered a bug, please let me know with a
[GitHub Issue](https://github.com/Micha-ohne-el/logbook/issues)! No need to be shy, there is no format to these Issues,
just type whatever you want :)

If you've already got experience with Kotlin, feel free to work on a fix or feature on your own and submitting a
[Pull Request](https://github.com/Micha-ohne-el/logbook/pulls)! There are no official contribution guidelines, just try your
best and I'll see if I can fix some issues if there are any :)

## To Do

* Make `by level` use the name of the property as the level name by default.
* Make `Logbook.WithDefaults`'s random logbook color stable (i.e. use hashing instead of randomness).
* Handle multi-line log messages (such as stack traces).
* Improve timestamp formatting by using Chunks as well.
	* Extend Chunks to be able to have a min and max width, padding, alignment, etc.
* Make ci pipeline upload test results on failure too.
