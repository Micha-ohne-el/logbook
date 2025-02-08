package moe.micha.logbook.outlets

import okio.Path
import okio.blackholeSink

internal actual fun openFile(path: Path) = blackholeSink()
