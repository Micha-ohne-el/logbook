package moe.micha.logbook.outlets

import okio.FileSystem
import okio.Path

internal actual fun openFile(path: Path) =
	FileSystem.SYSTEM.openReadWrite(path).appendingSink()
