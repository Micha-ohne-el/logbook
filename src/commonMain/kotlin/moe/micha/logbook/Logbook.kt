package moe.micha.logbook

import kotlin.random.Random
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import moe.micha.logbook.outlets.AnsiConsoleOutlet

open class Logbook(
	val name: String,
) : Colorable, CanFormat {
	open fun toChunk() = Chunk(name, colorInfo)

	@Suppress("LeakingThis")
	open class WithDefaults(
		name: String,
		random: Random = Random.Default,
	) : Logbook(name) {
		override fun format(entry: LogEntry) =
			listOf(
				Chunk("["),
				Chunk(entry.time.toLocalDateTime(TimeZone.currentSystemDefault()).toString()),
				Chunk("] "),
				entry.logbook.toChunk(),
				Chunk(" : "),
				entry.level.toChunk(),
				Chunk(" – "),
				Chunk(entry.data.toString()),
			)

		var baseRed = Color.fromHsl(-0.05, 0.9, 0.6)

		open var debug = LogLevel(this, "Debug", AnsiConsoleOutlet()).apply {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.6))
		}
		open var info = LogLevel(this, "Info", AnsiConsoleOutlet()).apply {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.4))
		}
		open var warning = LogLevel(this, "Warning", AnsiConsoleOutlet()).apply {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.2))
		}
		open var error = LogLevel(this, "Error", AnsiConsoleOutlet()).apply {
			colorInfo = ColorInfo(foreground = Color.pureWhite, background = baseRed)
		}

		override var colorInfo = ColorInfo(Color.fromHsl(random.nextDouble(), 1.0, 0.75))
	}

	override var formatter: ((LogEntry) -> Iterable<Chunk>)? = null

	fun formatWith(formatter: (LogEntry) -> Iterable<Chunk>) {
		this.formatter = formatter
	}
}
