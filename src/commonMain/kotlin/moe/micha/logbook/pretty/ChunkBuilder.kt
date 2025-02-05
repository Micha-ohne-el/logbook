package moe.micha.logbook.pretty

/**
 * The start of an ‘operator builder pattern’ chain to build a list of chunks.
 *
 * ## Usage
 * Simply write `StartChunks + x + y + z + ... + EndChunks` where `x`, `y` and `z` are [Chunk]s, [Chunkable]s, or [String]s.
 *
 * Don't forget to add the [EndChunks] object at the end!
 *
 * @sample moe.micha.logbook.Logbook.WithDefaults.format
 */
object StartChunks {
	operator fun plus(chunk: Chunk) = apply { chunks += chunk }
	operator fun plus(chunkable: Chunkable) = apply { chunks += chunkable.toChunk() }
	operator fun plus(string: String) = apply { chunks += Chunk(string) }

	@Suppress("UNUSED_PARAMETER")
	operator fun plus(end: EndChunks): List<Chunk> = chunks


	private val chunks = mutableListOf<Chunk>()
}

object EndChunks
