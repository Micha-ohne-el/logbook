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

/**
 * Base class for all logbooks.
 *
 * ## Usage
 * To create your own logbook, create a subclass of [Logbook].
 * You can make it an object, but it is recommended to use the following pattern:
 * ```kt
 * abstract class MyLog : Logbook() {
 *     companion object : MyLog()
 * }
 * ```
 * This way, you can create another logger that inherits from your base logger:
 * ```kt
 * abstract class MySpecificLog : MyLog() {
 *     companion object : MySpecificLog()
 * }
 * ```
 *
 * ## Note
 * The [Logbook] class itself comes with the most minimal configuration possible, **probably too minimal for you**.
 * For this reason, you can choose to use [Logbook.WithDefaults] instead, which comes preconfigured with four [LogLevel]s –
 * each set up with an [AnsiConsoleOutlet] – a nice format, and a randomized name color so you can tell different loggers apart.
 * See [Logbook.WithDefaults] for more info.
 */
abstract class Logbook : Colorable, CanFormat {
	open val name: String = this::class.simpleName ?: throw Error("Anonymous Logbooks must provide a name explicitly.")

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


	/**
	 * Convenience class that provides a lot of useful features that most logbooks will want.
	 * You can override anything you don't like, of course.
	 *
	 * ## Features
	 * ### Four preconfigured [LogLevel]s
	 * * [debug] – green text, no background
	 * * [info] – yellow text, no background
	 * * [warning] – red text, no background
	 * * [error] – white text, bright red background
	 *
	 * **The `minimumLogLevel` is set to [info].**
	 *
	 * ### There is a default format set
	 * ```
	 * [DD.MM.YYYY@hh:mm:ss.fff] LogbookName : LogLevelName – data
	 * ```
	 * `LogbookName` has a randomly assigned color per logbook. This helps to distinguish logbooks from one another.
	 * `LogLevelName` has a specific color per log level, as specified above.
	 *
	 * ### All levels output to an [AnsiConsoleOutlet].
	 */
	abstract class WithDefaults(random: Random = Random.Default) : Logbook() {
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

		protected var baseRed = Color.fromHsl(-0.05, 0.9, 0.6)

		open val debug by level("Debug", AnsiConsoleOutlet()) {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.8))
		}
		open val info by level("Info", AnsiConsoleOutlet()) {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.6))
		}
		open val warning by level("Warning", AnsiConsoleOutlet()) {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.4))
		}
		open val error by level("Error", AnsiConsoleOutlet()) {
			colorInfo = ColorInfo(foreground = Color.pureWhite, background = baseRed)
		}

		override var colorInfo: ColorInfo? = ColorInfo(Color.fromHsl(random.nextDouble(), 1.0, 0.75))

		init {
			@Suppress("LeakingThis")
			minimumLevel = info
		}
	}
}
