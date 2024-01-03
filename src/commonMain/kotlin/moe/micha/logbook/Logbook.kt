package moe.micha.logbook

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.random.Random
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import moe.micha.logbook.outlets.AnsiConsoleOutlet

open class Logbook(
	val name: String,
) : Colorable, CanFormat {
	open fun toChunk() = Chunk(name, colorInfo)

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

		open val debug by level("Debug", AnsiConsoleOutlet()) {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.6))
		}
		open val info by level("Info", AnsiConsoleOutlet()) {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.4))
		}
		open val warning by level("Warning", AnsiConsoleOutlet()) {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.2))
		}
		open val error by level("Error", AnsiConsoleOutlet()) {
			colorInfo = ColorInfo(foreground = Color.pureWhite, background = baseRed)
		}

		override var colorInfo = ColorInfo(Color.fromHsl(random.nextDouble(), 1.0, 0.75))
	}

	override var formatter: ((LogEntry) -> Iterable<Chunk>)? = null

	fun formatWith(formatter: (LogEntry) -> Iterable<Chunk>) {
		this.formatter = formatter
	}

	val levels = mutableListOf<LogLevel>()

	var minimumLevel: LogLevel? = levels.firstOrNull()
		set(value) {
			var enable = false
			for (level in levels) {
				if (level == value) enable = true
				level.isEnabled = enable
			}
			field = value
		}


	protected fun level(name: String, vararg outlets: LogOutlet, config: LogLevel.() -> Unit = {}) =
		PropertyDelegateProvider { thisRef: Logbook, _ ->
			val level = LogLevel(thisRef, name, *outlets).apply(config)
			thisRef.levels += level
			ReadOnlyProperty<Logbook, _> { _, _ -> level }
		}
}
