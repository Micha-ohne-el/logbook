package moe.micha.logbook.pretty

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

val Instant.local get() = toLocalDateTime(TimeZone.currentSystemDefault())

val Instant.utc get() = toLocalDateTime(TimeZone.UTC)

fun LocalDateTime.formatAsStandard() = toString()

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
	SimpleFormatSymbol('C', Alignment.Left, ' ') { dateTime ->
		if (dateTime.year < 0) "BC" else "AD"
	},
	SimpleFormatSymbol('E', Alignment.Left, ' ') { dateTime ->
		if (dateTime.year < 0) "BCE" else "CE"
	}
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
