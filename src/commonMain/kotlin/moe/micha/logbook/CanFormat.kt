package moe.micha.logbook

interface CanFormat {
	fun format(entry: LogEntry): Iterable<Chunk>? = formatter?.invoke(entry)

	val formatter: ((LogEntry) -> Iterable<Chunk>)? get() = null
}
