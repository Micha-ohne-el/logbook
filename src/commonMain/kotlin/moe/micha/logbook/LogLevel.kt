package moe.micha.logbook

import kotlinx.datetime.Clock

class LogLevel(
	val logbook: Logbook,
	val name: String,
	vararg outlets: LogOutlet,
) : Colorable, CanFormat {
	var outlets = outlets.toMutableSet()

	operator fun invoke(data: Any?) {
		val entry = LogEntry(
			time = Clock.System.now(),
			logbook = logbook,
			level = this,
			data = data,
		)

		for (outlet in outlets) {
			val formatted = outlet.format(entry) ?: format(entry) ?: logbook.format(entry) ?: listOf(Chunk(data.toString()))

			outlet.send(formatted)
		}
	}

	override var colorInfo: ColorInfo? = null

	fun toChunk() = Chunk(name, colorInfo)

	override var formatter: ((LogEntry) -> Iterable<Chunk>)? = null

	fun formatWith(formatter: (LogEntry) -> Iterable<Chunk>) {
		this.formatter = formatter
	}
}
