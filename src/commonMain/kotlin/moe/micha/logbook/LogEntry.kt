package moe.micha.logbook

import kotlin.time.TimeMark

data class LogEntry(
	val logbook: Logbook,
	val level: LogLevel,
	val time: TimeMark,
	val data: Any?,
)
