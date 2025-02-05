[![Kotlin 2.1.10](https://img.shields.io/badge/Kotlin%2FMultiplatform-2.1.10-7F52FF.svg?logo=kotlin)](http://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/moe.micha/logbook?label=Latest%20Version)](https://central.sonatype.com/artifact/moe.micha/logbook)
[![CI status badge](https://img.shields.io/github/actions/workflow/status/Micha-ohne-el/logbook/integration.yaml?label=CI)](https://github.com/Micha-ohne-el/logbook/actions/workflows/integration.yaml)
[![CD status badge](https://img.shields.io/github/actions/workflow/status/Micha-ohne-el/logbook/deployment.yaml?label=CD)](https://github.com/Micha-ohne-el/logbook/actions/workflows/deployment.yaml)
[![Documentation available](https://img.shields.io/badge/Documentation-available-blue)](https://logbook.micha.moe)
[![License – MIT](https://img.shields.io/github/license/Micha-ohne-el/logbook?color=gold&label=License)](https://github.com/Micha-ohne-el/logbook/blob/main/license.md)

# logbook

Kotlin/Multiplatform logging library.

## Overview

```kt
// Logbook.WithDefaults gives us default log levels, formatting, and a console outlet:
class ParserLog : Logbook.WithDefaults() {
    // the logger's name is inferred as "Parser" but can be overridden:
    // override val name = "Parsing"
    
    // declare a new log level that is lower than debug:
    val trace by level(placeBefore = debug) {
        colorInfo = ColorInfo(0x999999) // gray
    }
    
    // set the format of the logger, example output: 2025-02-04T20:46:55Z Parser - warning : something happened!!
    override fun format(entry: LogEntry) =
		StartChunks + entry.time.utc.formatAsStandard() + " " + entry.logbook + " - " + entry.level + " : " + data.toString() + EndChunks
    
    // set the minimum log level to trace:
    init {
        minimumLevel = trace
    }
    
    // add companion object to avoid having to manually construct instance:
    companion object : ParserLog()
}
```

## Installation

Simply add a dependency on logbook to your Gradle project:

```kt
repositories {
    mavenCentral()
}

// for Kotlin/Multiplatform projects:
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("moe.micha:logbook:$version")
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

* Handle multi-line log messages (such as stack traces).
* Improve timestamp formatting by using Chunks as well.
    * Extend Chunks to be able to have a min and max width, padding, alignment, etc.
    * Also support relative timestamp (relative to the start of the program or to the last log message, etc.)
* Add some sort of grouping mechanism (such as JavaScript's `console.group`).
    * Maybe provide two separate functions – `groupImplicitly` and `groupExplicitly`.
      `groupExplicitly` would take a block in which a sublogger is accessible, which prints to the group.
      `groupImplicitly` would cause all following calls to the logger to be treated as though they were done on a sublogger.
      Just some ideas.
* Add a simple file outlet.
    * Add option to either append to existing file or clear file on every boot.
* Add a rotating file outlet.
