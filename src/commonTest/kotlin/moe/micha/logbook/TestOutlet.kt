package moe.micha.logbook

import moe.micha.logbook.pretty.Chunk

class TestOutlet : LogOutlet {
	val logs = mutableListOf<Iterable<Chunk>>()

	override fun send(chunks: Iterable<Chunk>) {
		logs += chunks
	}

	override var formatter: ((LogEntry) -> Iterable<Chunk>)? = null
}
