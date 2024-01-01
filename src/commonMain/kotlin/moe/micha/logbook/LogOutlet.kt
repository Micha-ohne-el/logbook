package moe.micha.logbook

interface LogOutlet : CanFormat {
	fun send(chunks: Iterable<Chunk>)
}
