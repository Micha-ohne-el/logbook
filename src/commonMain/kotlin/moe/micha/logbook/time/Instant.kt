package moe.micha.logbook.time

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class Instant(
	private val instant: kotlinx.datetime.Instant,
) {
	/**
	 * The time of the log in the local timezone of the computer.
	 */
	val local get() = LocalTime(instant.toLocalDateTime(TimeZone.currentSystemDefault()))

	/**
	 * The time of the log in UTC.
	 */
	val utc get() = LocalTime(instant.toLocalDateTime(TimeZone.UTC))

	/**
	 * The amount of time since the first ever log was called.
	 */
	val sinceBoot get() = Duration(instant - bootTime)

	companion object {
		init {
			bootTime // we access the property here to rush its initialization
		}
	}
}

private val bootTime = Clock.System.now()
