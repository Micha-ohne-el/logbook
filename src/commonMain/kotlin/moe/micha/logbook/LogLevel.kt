package moe.micha.logbook

import kotlinx.datetime.Clock
import moe.micha.logbook.pretty.CanFormat
import moe.micha.logbook.pretty.Chunk
import moe.micha.logbook.pretty.ColorInfo
import moe.micha.logbook.pretty.Colorable

class LogLevel internal constructor(
	val logbook: Logbook,
	val name: String,
	vararg outlets: LogOutlet,
) : Colorable, CanFormat, HasOutlets {
	override var outlets = outlets.toMutableSet()

	operator fun invoke(data: Any?) {
		if (!isEnabled) return

		val entry = LogEntry(
			time = Clock.System.now(),
			logbook = logbook,
			level = this,
			data = data,
		)

		for (outlet in outlets + logbook.outlets) {
			val formatted = outlet.format(entry) ?: format(entry) ?: logbook.format(entry) ?: formatFallback(entry)

			outlet.send(formatted)
		}
	}

	override var colorInfo: ColorInfo? = null

	fun toChunk() = Chunk(name, colorInfo)

	override var formatter: ((LogEntry) -> Iterable<Chunk>)? = null

	var isEnabled = true


	private fun formatFallback(entry: LogEntry) = listOf(Chunk(entry.data.toString()))
}
