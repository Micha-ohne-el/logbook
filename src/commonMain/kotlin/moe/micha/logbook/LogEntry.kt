package moe.micha.logbook

import kotlinx.datetime.Instant

data class LogEntry(
	val logbook: Logbook,
	val level: LogLevel,
	val time: Instant,
	val data: Any?,
)
