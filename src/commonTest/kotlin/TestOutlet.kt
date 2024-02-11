import moe.micha.logbook.LogEntry
import moe.micha.logbook.LogOutlet
import moe.micha.logbook.pretty.Chunk

class TestOutlet : LogOutlet {
	val logs = mutableListOf<Iterable<Chunk>>()

	override fun send(chunks: Iterable<Chunk>) {
		logs += chunks
	}

	override var formatter: ((LogEntry) -> Iterable<Chunk>)? = null
}
