package moe.micha.logbook.outlets

import moe.micha.logbook.LogOutlet
import moe.micha.logbook.pretty.Chunk
import moe.micha.logbook.pretty.ColorInfo

@Suppress("RemoveRedundantQualifierName")
abstract class ConsoleOutlet : LogOutlet {
	override fun send(chunks: Iterable<Chunk>) {
		for (chunk in chunks) {
			print(chunk)
		}

		flush()
	}


	protected open fun print(chunk: Chunk) = kotlin.io.print(colorizeIfNeeded(chunk))
	protected open fun flush() = kotlin.io.println()

	protected abstract fun colorize(text: String, colorInfo: ColorInfo): String

	private fun colorizeIfNeeded(chunk: Chunk) =
		if (chunk.colorInfo != null) colorize(chunk.text, chunk.colorInfo) else chunk.text
}
