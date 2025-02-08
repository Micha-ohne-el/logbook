package moe.micha.logbook

import moe.micha.logbook.pretty.Chunk
import moe.micha.logbook.pretty.Chunkable
import moe.micha.logbook.time.Instant

data class LogEntry(
	val logbook: Logbook,
	val level: LogLevel,
	val time: Instant,
	val data: Any?,
) {
	/**
	 * Use this function to format log messages.
	 *
	 * ## Examples:
	 * ```kt
	 * entry.format {
	 *     logbook * "/" * level * ": " * data.toString()
	 * } // result: "MyLogbook/warning: log message"
	 * ```
	 * ```kt
	 * entry.format {
	 *     time.sinceBoot.inSeconds * " | " * level * "@" * logbook * " - " data.toString()
	 * } // result: "12345.6789 | warning@MyLogbook - log message"
	 * ```
	 *
	 * @sample moe.micha.logbook.Logbook.WithDefaults.format
	 */
	inline fun format(block: Formatter.() -> List<Chunk>) = Formatter(this).block()

	inner class Formatter(
		val logbook: Logbook,
		val level: LogLevel,
		val time: Instant,
		val data: Any?,
	) {
		constructor(entry: LogEntry) : this(entry.logbook, entry.level, entry.time, entry.data)

		operator fun String.times(chunk: Chunk) = Chunk(this) * chunk
		operator fun String.times(chunkable: Chunkable) = Chunk(this) * chunkable.toChunk()
		operator fun String.times(string: String) = Chunk(this) * Chunk(string)
		operator fun Chunk.times(chunk: Chunk) = mutableListOf(this, chunk)
		operator fun Chunk.times(chunkable: Chunkable) = this * chunkable.toChunk()
		operator fun Chunk.times(string: String) = this * Chunk(string)
		operator fun MutableList<Chunk>.times(chunk: Chunk) = apply { add(chunk) }
		operator fun MutableList<Chunk>.times(chunkable: Chunkable) = this * chunkable.toChunk()
		operator fun MutableList<Chunk>.times(string: String) = this * Chunk(string)
	}
}
