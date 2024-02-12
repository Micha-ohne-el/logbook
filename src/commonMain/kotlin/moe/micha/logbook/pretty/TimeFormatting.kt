package moe.micha.logbook.pretty

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val Instant.local get() = toLocalDateTime(TimeZone.currentSystemDefault())

val Instant.utc get() = toLocalDateTime(TimeZone.UTC)

fun LocalDateTime.formatAsStandard() = toString()

/**
 * Format according to a simple (but limited) formatting pattern.
 *
 * Each letter in the pattern corresponds to one particular part of a timestamp.
 * Repeating a letter sets the minimum length of that particular part.
 * For instance, where `h:m:s` would produce `1:2:3`, `hh:mm:ss` would produce `01:02:02`.
 * All parts are padded with 0s and right-aligned, except `f` (fraction of a second) which is left-aligned.
 * This allows simple usage in the most common cases. For example:
 * `mm:hh:ss.fff` displays the time up to the millisecond, while `mm:hh:ss.ffffff` shows microseconds.
 * One more special case is `Y`, which is a 4-digit number, except when specified only once or twice,
 * in which case it is treated like a two-digit number.
 * I.e. `YYYYYY` produces `002024` while `YY` produces `24` (omitting the century).
 *
 * Supported letters are:
 * * `Y` – the year (4 digits or 2 digits as mentioned above)
 * * `M` – the month of the year (1 to 12)
 * * `D` – the day of the month (1 to 31)
 * * `h` – the hour of the day (0 to 23)
 * * `m` – the minute of the hour (0 to 59)
 * * `s` – the second of the minute (0 to 59)
 * * `f` – the fraction of the second (think of this as the fractional part of a number between 0 and 1)
 */
fun LocalDateTime.formatWithSimplePattern(pattern: String): String {
	var formatted = pattern

	for (symbol in simpleFormat) {
		formatted = formatted.replace(Regex(Regex.escape(symbol.key.toString()) + "+")) { matchResult ->
			val requestedLength = matchResult.value.length
			val replacement = symbol.getValue(this, requestedLength)

			when (symbol.alignment) {
				Alignment.Right -> replacement.padStart(requestedLength, symbol.padding)
				Alignment.Left -> replacement.padEnd(requestedLength, symbol.padding)
			}
		}
	}

	return formatted
}


private val simpleFormat = setOf(
	SimpleFormatSymbol('Y') { dateTime, width ->
		if (width <= 2) {
			dateTime.year.mod(100)
		} else {
			dateTime.year
		}
	},
	SimpleFormatSymbol('M', converter = LocalDateTime::monthNumber),
	SimpleFormatSymbol('D', converter = LocalDateTime::dayOfMonth),
	SimpleFormatSymbol('h', converter = LocalDateTime::hour),
	SimpleFormatSymbol('m', converter = LocalDateTime::minute),
	SimpleFormatSymbol('s', converter = LocalDateTime::second),
	SimpleFormatSymbol('f', Alignment.Left) { dateTime, width ->
		val nanoseconds = dateTime.nanosecond.toString()

		nanoseconds.substring(0, minOf(nanoseconds.length, width))
	},
)

private abstract class SimpleFormatSymbol(
	val key: Char,
	val alignment: Alignment = Alignment.Right,
	val padding: Char = '0',
) {
	abstract fun getValue(dateTime: LocalDateTime, width: Int): String

	companion object {
		operator fun invoke(
			key: Char,
			alignment: Alignment = Alignment.Right,
			padding: Char = '0',
			converter: (dateTime: LocalDateTime, width: Int) -> Any,
		) = object : SimpleFormatSymbol(key, alignment, padding) {
			override fun getValue(dateTime: LocalDateTime, width: Int) = converter(dateTime, width).toString()
		}

		operator fun invoke(
			key: Char,
			alignment: Alignment = Alignment.Right,
			padding: Char = '0',
			converter: (dateTime: LocalDateTime) -> Any,
		) = object : SimpleFormatSymbol(key, alignment, padding) {
			override fun getValue(dateTime: LocalDateTime, width: Int) = converter(dateTime).toString()
		}
	}
}

private enum class Alignment {
	Left,
	Right,
}
