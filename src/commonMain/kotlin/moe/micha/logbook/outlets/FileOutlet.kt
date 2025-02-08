package moe.micha.logbook.outlets

import moe.micha.logbook.LogEntry
import moe.micha.logbook.LogOutlet
import moe.micha.logbook.pretty.Chunk
import okio.BufferedSink
import okio.Path
import okio.Sink
import okio.buffer

/**
 * An outlet that writes log messages to a real file on the filesystem.
 *
 * Only the text of chunks provided is written to the file, all coloring is ignored.
 *
 * ## Compatibility note
 * **In JavaScript (browser and nodejs), this outlet does nothing!**
 */
class FileOutlet(
	val sink: BufferedSink,
) : LogOutlet {
	constructor(path: Path) : this(openFile(path).buffer())

	override fun send(chunks: Iterable<Chunk>) {
		for (chunk in chunks) {
			sink.writeUtf8(chunk.text)
		}

		sink.writeUtf8("\n")

		sink.flush()
	}

	override var formatter: ((LogEntry) -> Iterable<Chunk>)? = null
}

internal expect fun openFile(path: Path): Sink
