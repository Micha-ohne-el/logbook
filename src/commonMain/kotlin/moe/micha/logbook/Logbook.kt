package moe.micha.logbook

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
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
abstract class Logbook(
	private val normalizeName: NameNormalizer = defaultNameNormalizer,
) : Colorable, CanFormat, HasOutlets {
	open val name: String by lazy {
		normalizeName(this::class)
	}

	open fun toChunk() = Chunk(name, colorInfo)

	override var colorInfo: ColorInfo? = null
	override var formatter: ((LogEntry) -> Iterable<Chunk>)? = null
	override var outlets = mutableSetOf<LogOutlet>()

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
		name: String? = null,
		placeBefore: LogLevel?,
		vararg outlets: LogOutlet,
		config: LogLevel.() -> Unit = {},
	) =
		PropertyDelegateProvider { thisRef: Logbook, property ->
			val level = LogLevel(thisRef, name ?: property.name, *outlets).apply(config)

			when (placeBefore) {
				null -> thisRef.levels += level
				!in thisRef.levels -> throw IllegalArgumentException("Could not place new level before level $placeBefore because said level is not registered on this LogBook.")
				else -> thisRef.levels.add(thisRef.levels.indexOf(placeBefore), level)
			}

			ReadOnlyProperty<Logbook, _> { _, _ -> level }
		}

	protected fun level(placeBefore: LogLevel?, vararg outlets: LogOutlet, config: LogLevel.() -> Unit = {}) =
		level(name = null, placeBefore, outlets = outlets, config)

	protected fun level(name: String, vararg outlets: LogOutlet, config: LogLevel.() -> Unit = {}) =
		level(name, placeBefore = null, outlets = outlets, config)

	protected fun level(vararg outlets: LogOutlet, config: LogLevel.() -> Unit = {}) =
		level(name = null, placeBefore = null, outlets = outlets, config)


	/**
	 * Convenience class that provides a lot of useful features that most logbooks will want.
	 * You can override anything you don't like, of course.
	 *
	 * ## Features
	 * ### Four preconfigured [LogLevel]s
	 * * [debug] – cyan text, no background
	 * * [info] – green text, no background
	 * * [warning] – yellow text, no background
	 * * [error] – white text, bright red background
	 *
	 * **The [minimumLevel] is set to [info].**
	 *
	 * ### There is a default format set
	 * ```
	 * [DD.MM.YYYY@hh:mm:ss.fff] LogbookName : LogLevelName – data
	 * ```
	 * `LogbookName` has a randomly assigned color per logbook. This helps to distinguish logbooks from one another.
	 * `LogLevelName` has a specific color per log level, as specified above.
	 *
	 * ### An [AnsiConsoleOutlet] is preconfigured for the whole logbook.
	 */
	abstract class WithDefaults(nameNormalizer: NameNormalizer = defaultNameNormalizer) : Logbook(nameNormalizer) {
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

		override var outlets: MutableSet<LogOutlet> = mutableSetOf(AnsiConsoleOutlet())

		protected var baseRed = Color.fromHsl(-0.05, 0.9, 0.6)

		val debug by level {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.8))
		}
		val info by level {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.6))
		}
		val warning by level {
			colorInfo = ColorInfo(foreground = baseRed.copyHsl(baseRed.hue + 0.4))
		}
		val error by level {
			colorInfo = ColorInfo(foreground = Color.pureWhite, background = baseRed)
		}

		override var colorInfo: ColorInfo? = null
			get() = field ?: run {
				field = ColorInfo(Color.fromHsl(name.hashCode().toDouble() / Int.MAX_VALUE, 1.0, 0.75))
				field
			}


		init {
			minimumLevel = info
		}
	}
}
