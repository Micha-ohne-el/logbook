package moe.micha.logbook

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.random.Random
import moe.micha.logbook.outlets.AnsiConsoleOutlet
import moe.micha.logbook.pretty.CanFormat
import moe.micha.logbook.pretty.Chunk
import moe.micha.logbook.pretty.Color
import moe.micha.logbook.pretty.ColorInfo
import moe.micha.logbook.pretty.Colorable
import moe.micha.logbook.pretty.formatWithSimplePattern
import moe.micha.logbook.pretty.local

open class Logbook(
	val name: String,
) : Colorable, CanFormat {
	open fun toChunk() = Chunk(name, colorInfo)

	override var colorInfo: ColorInfo? = null
	override var formatter: ((LogEntry) -> Iterable<Chunk>)? = null

	val levels = mutableListOf<LogLevel>()

	var minimumLevel: LogLevel? = null
		get() = field ?: run {
			field = levels.firstOrNull()
			field
		}
		set(value) {
			var enable = false
			for (level in levels) {
				if (level == value) enable = true
				level.isEnabled = enable
			}
			field = value
		}


	protected fun level(
		name: String,
		placeBefore: LogLevel?,
		vararg outlets: LogOutlet,
		config: LogLevel.() -> Unit = {},
	) =
		PropertyDelegateProvider { thisRef: Logbook, _ ->
			val level = LogLevel(thisRef, name, *outlets).apply(config)

			when (placeBefore) {
				null -> thisRef.levels += level
				!in thisRef.levels -> throw IllegalArgumentException("Could not place new level before level $placeBefore because said level is not registered on this LogBook.")
				else -> thisRef.levels.add(thisRef.levels.indexOf(placeBefore), level)
			}

			ReadOnlyProperty<Logbook, _> { _, _ -> level }
		}

	protected fun level(name: String, vararg outlets: LogOutlet, config: LogLevel.() -> Unit = {}) =
		level(name, placeBefore = null, outlets = outlets, config)


	open class WithDefaults(
		name: String,
		random: Random = Random.Default,
	) : Logbook(name) {
		override fun format(entry: LogEntry) =
			listOf(
				Chunk("["),
				Chunk(entry.time.local.formatWithSimplePattern("DD.MM.YYYY@hh:mm:ss.fff")),
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

		override var colorInfo: ColorInfo? = ColorInfo(Color.fromHsl(random.nextDouble(), 1.0, 0.75))
	}
}
