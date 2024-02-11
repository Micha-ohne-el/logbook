package moe.micha.logbook.outlets

import moe.micha.logbook.LogEntry
import moe.micha.logbook.LogOutlet
import moe.micha.logbook.pretty.Chunk

open class ConsoleOutlet : LogOutlet {
	override fun send(chunks: Iterable<Chunk>) {
		for (chunk in chunks) {
			print(chunk)
		}

		flush()
	}

	override var formatter: ((LogEntry) -> Iterable<Chunk>)? = null


	protected open fun print(chunk: Chunk) = print(chunk.text)
	protected open fun flush() = println()
}
