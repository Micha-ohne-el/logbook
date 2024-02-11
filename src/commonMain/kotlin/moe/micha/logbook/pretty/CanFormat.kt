package moe.micha.logbook.pretty

import moe.micha.logbook.LogEntry

interface CanFormat {
	fun format(entry: LogEntry): Iterable<Chunk>? = formatter?.invoke(entry)

	var formatter: ((LogEntry) -> Iterable<Chunk>)?

	fun formatWith(formatter: (LogEntry) -> Iterable<Chunk>) {
		this.formatter = formatter
	}
}
