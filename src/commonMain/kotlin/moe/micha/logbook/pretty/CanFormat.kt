package moe.micha.logbook.pretty

import moe.micha.logbook.LogEntry

interface CanFormat {
	fun format(entry: LogEntry): Iterable<Chunk>? = formatter?.invoke(entry)

	val formatter: ((LogEntry) -> Iterable<Chunk>)? get() = null
}
