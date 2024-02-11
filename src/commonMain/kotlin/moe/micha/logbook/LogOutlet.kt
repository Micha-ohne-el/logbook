package moe.micha.logbook

import moe.micha.logbook.pretty.CanFormat
import moe.micha.logbook.pretty.Chunk

interface LogOutlet : CanFormat {
	fun send(chunks: Iterable<Chunk>)

	override var formatter: ((LogEntry) -> Iterable<Chunk>)?

	fun formatWith(formatter: (LogEntry) -> Iterable<Chunk>) {
		this.formatter = formatter
	}
}
