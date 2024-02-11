package moe.micha.logbook.outlets

import moe.micha.logbook.LogEntry
import moe.micha.logbook.LogOutlet
import moe.micha.logbook.pretty.Chunk

/**
 * Base class for all console outlets.
 *
 * Prints all chunks received using consecutive calls to [print], then calls [flush].
 * Both of those can be overridden.
 */
open class ConsoleOutlet : LogOutlet {
	override fun send(chunks: Iterable<Chunk>) {
		for (chunk in chunks) {
			print(chunk)
		}

		flush()
	}

	override var formatter: ((LogEntry) -> Iterable<Chunk>)? = null


	/**
	 * Prints the [chunk] to the console.
	 *
	 * This should not print a trailing newline!
	 *
	 * By default, this only prints [Chunk.text], ignoring any other property [Chunk] may have.
	 */
	protected open fun print(chunk: Chunk) = print(chunk.text)

	/**
	 * Ends the log entry (by default by printing a newline).
	 */
	protected open fun flush() = println()
}
