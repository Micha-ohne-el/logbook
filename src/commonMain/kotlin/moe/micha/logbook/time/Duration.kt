package moe.micha.logbook.time

class Duration(
	private val duration: kotlin.time.Duration,
) {
	val inMicroseconds: Double get() = duration.inWholeNanoseconds / 1000.0
	val inMilliseconds: Double get() = inMicroseconds / 1000.0
	val inSeconds: Double get() = inMilliseconds / 1000.0

	/**
	 * Format this [Duration] in the form `1d 2h 3m 4s 5ms 6us`.
	 * All components are whole numbers greater than 0.
	 * Components which have the value 0 are omitted.
	 * If all components were to be omitted, `0us` is the result.
	 */
	fun formatAsMultiUnitString() = duration.toComponents { days, hours, minutes, seconds, nanoseconds ->
		val microseconds = nanoseconds / 1_000 % 1_000
		val milliseconds = nanoseconds / 1_000_000 % 1_000_000
		buildString {
			if (days > 0) append("${days}d ")
			if (hours > 0) append("${hours}h ")
			if (minutes > 0) append("${minutes}m ")
			if (seconds > 0) append("${seconds}s ")
			if (milliseconds > 0) append("${milliseconds}ms ")
			if (microseconds > 0) append("${microseconds}us ")
			if (isEmpty()) append("0us")
		}.trimEnd(' ')
	}
}
