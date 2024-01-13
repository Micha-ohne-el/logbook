package moe.micha.logbook.outlets

import moe.micha.logbook.pretty.Chunk
import moe.micha.logbook.pretty.ColorInfo

abstract class ColoredConsoleOutlet : ConsoleOutlet() {
	override fun print(chunk: Chunk) = print(colorizeIfNeeded(chunk))


	protected abstract fun colorize(text: String, colorInfo: ColorInfo): String

	private fun colorizeIfNeeded(chunk: Chunk) =
		if (chunk.colorInfo != null) colorize(chunk.text, chunk.colorInfo) else chunk.text
}
